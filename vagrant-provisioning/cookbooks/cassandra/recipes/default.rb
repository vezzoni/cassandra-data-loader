template 'datastax.repo' do
  path "/etc/yum.repos.d/datastax.repo"
  source 'datastax.repo.erb'
end

package 'dsc20' do
  action :install
end

template 'cassandra.yaml' do
  path "/etc/cassandra/default.conf/cassandra.yaml"
  source 'cassandra.yaml.erb'
end

service 'cassandra' do
  supports :restart => true, :reload => true, :status => true
  action [:enable, :start]
end
