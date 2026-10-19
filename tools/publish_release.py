"""Publish validated public jars without replacing assets or resetting download counters."""
import hashlib,json,os,re,subprocess,sys,zipfile
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
def run(*args):
 return subprocess.run(['gh',*args],text=True,capture_output=True,check=True)
def version_from(root):
 matches=re.findall(r'^version=([0-9]+\.[0-9]+\.[0-9]+)\s*$',(root/'gradle.properties').read_text(),re.M)
 if len(matches)!=1:raise ValueError('Expected one public semantic version in gradle.properties')
 return matches[0]
def validated_assets(root,version):
 assets=[]
 for mc,suffix in [('26.3',''),('26.2','+mc26.2')]:
  path=root/'downloads'/f'techmines-plus-{version}{suffix}.jar'
  with zipfile.ZipFile(path)as jar:
   metadata=json.loads(jar.read('fabric.mod.json'))
   if metadata['id']!='techmines_plus' or metadata['version']!=version+suffix or metadata['depends']['minecraft']!=mc:raise ValueError('Unexpected public build metadata: '+path.name)
  assets.append(path)
 return assets
def main():
 repo=os.environ['GH_REPO'];target=os.environ['GITHUB_SHA'];version=version_from(ROOT);tag='v'+version
 assets=validated_assets(ROOT,version)
 try:release=json.loads(run('api',f'repos/{repo}/releases/tags/{tag}').stdout)
 except subprocess.CalledProcessError as error:
  if '404' not in error.stderr:raise
  release=None
 if release is None:
  notes=ROOT/'release-notes'/f'{version}.md'
  args=['release','create',tag,*map(str,assets),'--target',target,'--title','Techmines Plus '+version]
  if notes.is_file():args+=['--notes-file',str(notes)]
  else:args+=['--notes',f'Public Techmines Plus {version}. Choose the jar matching Minecraft 26.3 or 26.2. Requires Java 25, Fabric Loader and the matching Fabric API.']
  print(run(*args).stdout)
 else:
  if release['draft']:raise ValueError('An unfinished draft exists; review it before publishing')
  existing={a['name']:a for a in release['assets']}
  for path in assets:
   if path.name in existing:
    digest=existing[path.name].get('digest')
    expected='sha256:'+hashlib.sha256(path.read_bytes()).hexdigest()
    if digest and digest!=expected:raise ValueError('Existing release asset differs. Bump the version; do not replace counted downloads.')
    print('Preserved existing attachment and download count: '+path.name)
   else:print(run('release','upload',tag,str(path)).stdout)
 release=json.loads(run('api',f'repos/{repo}/releases/tags/{tag}').stdout)
 print(release['html_url'])
 for asset in release['assets']:print(f"{asset['name']}: {asset['download_count']} downloads")
if __name__=='__main__':main()
