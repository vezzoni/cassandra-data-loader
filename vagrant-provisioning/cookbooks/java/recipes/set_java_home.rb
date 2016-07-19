ruby_block 'set-env-java-home' do
  block do
    ENV['JAVA_HOME'] = node['java']['java_home']
  end
  not_if { ENV['JAVA_HOME'] == node['java']['java_home'] }
end

directory '/etc/profile.d' do
  mode 00755
end

file '/etc/profile.d/jdk.sh' do
  content "export JAVA_HOME=#{node['java']['java_home']}"
  mode 00755
end

ruby_block 'Set JAVA_HOME in /etc/environment' do
  block do
    file = Chef::Util::FileEdit.new('/etc/environment')
    file.insert_line_if_no_match(/^JAVA_HOME=/, "JAVA_HOME=#{node['java']['java_home']}")
    file.search_file_replace_line(/^JAVA_HOME=/, "JAVA_HOME=#{node['java']['java_home']}")
    file.write_file
    only_if { node['java']['set_etc_environment'] }
  end
end
