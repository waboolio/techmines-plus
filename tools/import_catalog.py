import openpyxl,re,json,sys,pathlib
source=pathlib.Path(sys.argv[1]); values=openpyxl.load_workbook(source,data_only=True); styles=openpyxl.load_workbook(source,data_only=False)
names=['Spawn','Nether','End','Heaven','Ocean','Acidic Lakes','Steampunk','Sakura Forest','Wild West','Pixel Grove','Planet X'];worlds=[];total=0;estimated=0
for name in names:
 mines=[]
 if name in values.sheetnames:
  s=values[name];f=styles[name]
  def display(row,col):
   global estimated
   v=s.cell(row,col).value;cell=f.cell(row,col)
   if v is None:return 'Not recorded'
   est=bool(cell.font.color and cell.font.color.type=='rgb' and str(cell.font.color.rgb)[-6:].upper() in ['0070C0','C55A11'])
   if isinstance(v,(int,float)):
    if cell.data_type!='f' and isinstance(v,float) and not v.is_integer():return str(v)+' (unit unconfirmed)'
    if abs(v)>=1e9:text=f'{v/1e9:.2f}B'
    elif abs(v)>=1e6:text=f'{v/1e6:.2f}M'
    elif abs(v)>=1000:text=f'{v/1000:.2f}k'
    else:text=str(round(v))
   else:
    text=str(v).strip().replace(' ','')
    if not re.fullmatch(r'[0-9.,]+[kKmMbB]?',text):return 'Not confirmed'
   if est:estimated+=1;return '~'+text
   return text
  def exact_cost(row,col):
   v=s.cell(row,col).value;cell=f.cell(row,col)
   if v is None:return 'Not recorded'
   est=bool(cell.font.color and cell.font.color.type=='rgb' and str(cell.font.color.rgb)[-6:].upper() in ['0070C0','C55A11'])
   if isinstance(v,(int,float)):
    if cell.data_type!='f' and isinstance(v,float) and not v.is_integer():return 'Not confirmed'
    from decimal import Decimal
    text=format(Decimal(str(v)).quantize(Decimal('1')),'f')
   else:
    text=str(v).strip().replace(' ','')
    if not re.fullmatch(r'[0-9.,]+[kKmMbB]?',text):return 'Not confirmed'
   return ('~' if est else '')+text
  for start in range(1,s.max_row+1):
   title=str(s.cell(start,3).value or '').strip()
   if not re.match(r'MINE\s+\d+|Boss',title,re.I):continue
   header=start+4;row=start+5;sword=2 if str(s.cell(header,2).value).strip().lower()=='sword' else 3
   tool=4 if str(s.cell(header,4).value).strip().lower() in ['pickaxe','axe','shovel','hoe'] else 3
   toolname=str(s.cell(header,tool).value or 'Tool').title()
   resource=str(s.cell(start+2,3).value or '').partition(':')[2].strip() or str(s.cell(start+2,4).value or '').strip()
   rows=[{'tier':str(s.cell(row+i,1).value or ['I','II','III','IV','V'][i]),'sword':display(row+i,sword),'tool':display(row+i,tool),'armor':display(row+i,5)} for i in range(5)]
   talisman=display(row,6);elytra=display(row,7)
   if not resource:resource=re.sub(r'^MINE\s+\d+\s*','',title,flags=re.I)
   orange=any(f.cell(r,c).font.color and f.cell(r,c).font.color.type=='rgb' and f.cell(r,c).font.color.rgb=='FFC55A11' for r in range(row,row+5) for c in range(2,8))
   notes=['Cross-world estimates: verify vendor prices.'] if orange else ['~ marks estimates; verify vendor prices.']
   if 'unit unconfirmed' in talisman:notes=['Talisman unit missing in source; verify with vendor.']
   for nr in range(row,row+5):
    review=str(s.cell(nr,9).value or '')
    if review.startswith('REVIEW:'):notes.append(review)
   mines.append({'name':re.sub(r'\s+',' ',title),'resource':resource,'toolName':toolname,'rows':rows,'talisman':talisman,'elytra':elytra,'notes':notes,'additionalTotalCosts':[exact_cost(row+i,c) for c in range(2,5) if c!=tool and str(s.cell(header,c).value).lower() in ['pickaxe','axe','shovel','hoe'] for i in range(5) if s.cell(row+i,c).value is not None],'totalCosts':[exact_cost(row+i,sword) for i in range(5)]+[exact_cost(row+i,tool) for i in range(5)]+[exact_cost(row+i,5) for i in range(3)]+[exact_cost(row,6),exact_cost(row,7)],'source':f'Techmines.xlsx / {name} / {openpyxl.utils.get_column_letter(sword)}{start}:G{row+4}'})
 worlds.append({'name':name,'mines':mines});total+=len(mines)
result={'schemaVersion':1,'source':'Techmines.xlsx; blue/orange estimates marked ~; black entries confirmed; imported 2026-10-18','worlds':worlds}
pathlib.Path(sys.argv[2]).write_text(json.dumps(result,indent=2),encoding='utf-8')
assert len(worlds)==11
assert all(len(m['rows'])==5 for w in worlds for m in w['mines'])
assert not next(m for w in worlds if w['name']=='Heaven' for m in w['mines'] if 'Cloudium' in m['name'])['rows'][0]['sword'].startswith('~')
assert next(m for w in worlds if w['name']=='Ocean' for m in w['mines'] if 'Prismarine' in m['name'])['rows'][0]['sword']=='1.27M'
print(f'Imported {total} mine/boss pages, 11 worlds, {estimated} estimated prices marked ~')
