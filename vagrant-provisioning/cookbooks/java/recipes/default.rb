java_home = node['java']['java_home']
arch = node['java']['arch']

tarball_url = node['java']['jdk']['8'][arch]['url']
tarball_checksum = node['java']['jdk']['8'][arch]['checksum']
bin_cmds = node['java']['jdk']['8']['bin_cmds']

include_recipe 'java::set_java_home'

package 'tar'

java_ark 'jdk' do
  url tarball_url
  default node['java']['set_default']
  checksum tarball_checksum
  app_home java_home
  bin_cmds bin_cmds
  alternatives_priority node['java']['alternatives_priority']
  retries node['java']['ark_retries']
  retry_delay node['java']['ark_retry_delay']
  connect_timeout node['java']['ark_timeout']
  use_alt_suffix node['java']['use_alt_suffix']
  reset_alternatives node['java']['reset_alternatives']
  download_timeout node['java']['ark_download_timeout']
  action :install
end

include_recipe 'java::oracle_jce' if node['java']['oracle']['jce']['enabled']
