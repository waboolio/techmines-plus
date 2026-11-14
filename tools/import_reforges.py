import sys,json,pathlib,openpyxl
from decimal import Decimal
book=openpyxl.load_workbook(sys.argv[1],data_only=True);sheet=book['Reforges']
names=['Common','Uncommon','Rare','Epic','Legendary','Mythical','Divine','Secret']
colours={'White':'#FFFFFF','Yellow':'#FFFF55','Dark blue':'#0000AA','Purple':'#AA00AA','Orange':'#FFAA00','Pink':'#FF55FF','Light blue':'#55FFFF','Red':'#FF5555'}
tiers={};reforges={}
for values in sheet.iter_rows(min_row=6,values_only=True):
 name,types,tier,letter,colour,stat1,bonus1,stat2,bonus2,source=values[:10]
 if not name:continue
 index=names.index(tier);entry=reforges.setdefault(name,{'name':name,'itemTypes':[t.strip() for t in types.split(',')],'bonuses':{},'source':source})
 definition={'name':tier,'letter':letter,'colour':colour,'hex':colours[colour]}
 if tier in tiers:assert tiers[tier]==definition
 tiers[tier]=definition
 for stat,bonus in [(stat1,bonus1),(stat2,bonus2)]:
  if not stat:assert bonus is None;continue
  assert isinstance(bonus,(int,float));rates=entry['bonuses'].setdefault(stat,[None]*8);assert rates[index] is None;rates[index]=float(Decimal(str(bonus))*100)
assert len(reforges)==21 and len(tiers)==8
assert all(all(x is not None for x in rates) for r in reforges.values() for rates in r['bonuses'].values())
result={'schemaVersion':1,'tiers':[tiers[t] for t in names],'reforges':list(reforges.values())}
pathlib.Path(sys.argv[2]).write_text(json.dumps(result,indent=2),encoding='utf-8')
print('Imported 21 reforges, 168 tier definitions from Reforges sheet.')
