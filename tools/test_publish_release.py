import importlib.util,json,os,subprocess,unittest,hashlib
from unittest.mock import patch
from pathlib import Path
spec=importlib.util.spec_from_file_location('publisher',Path(__file__).with_name('publish_release.py'));publisher=importlib.util.module_from_spec(spec);spec.loader.exec_module(publisher)
class PublishChecks(unittest.TestCase):
 def setUp(self):
  self.assets=publisher.validated_assets(publisher.ROOT,'0.8.0');self.calls=[]
  self.release={'draft':False,'html_url':'https://github.com/waboolio/techmines-plus/releases/tag/v0.8.0','assets':[{'name':p.name,'digest':'sha256:'+hashlib.sha256(p.read_bytes()).hexdigest(),'download_count':n}for p,n in zip(self.assets,[7,3])]}
 def execute(self,handler):
  with patch.dict(os.environ,{'GH_REPO':'waboolio/techmines-plus','GITHUB_SHA':'test-commit'}),patch.object(publisher,'run',side_effect=handler):publisher.main()
 def test_new_release_attaches_both_public_jars(self):
  def handler(*args):
   self.calls.append(args)
   if len(self.calls)==1:raise subprocess.CalledProcessError(1,args,stderr='HTTP 404: Not Found')
   return subprocess.CompletedProcess(args,0,stdout=json.dumps(self.release)if args[0]=='api'else 'created')
  self.execute(handler);created=[c for c in self.calls if c[:2]==('release','create')];self.assertEqual(len(created),1)
  for p in self.assets:self.assertIn(str(p),created[0])
 def test_repeat_run_preserves_counts(self):
  def handler(*args):self.calls.append(args);return subprocess.CompletedProcess(args,0,stdout=json.dumps(self.release))
  self.execute(handler);self.assertTrue(all(c[0]=='api'for c in self.calls));self.assertEqual([a['download_count']for a in self.release['assets']],[7,3])
 def test_changed_asset_cannot_reset_counter(self):
  self.release['assets'][0]['digest']='sha256:different'
  with self.assertRaisesRegex(ValueError,'Bump the version'):self.execute(lambda *a:subprocess.CompletedProcess(a,0,stdout=json.dumps(self.release)))
 def test_missing_attachment_is_added_without_clobber(self):
  self.release['assets']=self.release['assets'][:1]
  def handler(*args):self.calls.append(args);return subprocess.CompletedProcess(args,0,stdout=json.dumps(self.release)if args[0]=='api'else 'uploaded')
  self.execute(handler);uploads=[c for c in self.calls if c[:2]==('release','upload')];self.assertEqual(len(uploads),1);self.assertIn(str(self.assets[1]),uploads[0]);self.assertNotIn('--clobber',uploads[0])
if __name__=='__main__':unittest.main()
