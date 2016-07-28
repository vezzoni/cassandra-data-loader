ENV["LC_ALL"] = "en_US.UTF-8"

VAGRANTFILE_API_VERSION = '2'

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|

  config.vm.box = "centos-7-x86_64"
  config.vm.box_url = "http://github.com/vezzoni/vagrant-vboxes/releases/download/0.0.1/centos-7-x86_64.box"
  config.vm.box_check_update = false

  config.ssh.insert_key = false
  config.ssh.forward_agent = true

  config.vm.provider "virtualbox" do |v|
    v.name = "cassandra.data.loader"
    v.gui = false
    v.memory = "1024"
  end
  config.vm.hostname = "cassandra.data.loader"

  config.vm.network :forwarded_port, guest: 9042, host: 9042 # binary port (Thrift)
  config.vm.network :forwarded_port, guest: 9160, host: 9160

  config.vm.synced_folder "~/.m2", "/home/vagrant/.m2"
  config.vm.synced_folder ".", "/home/vagrant", type: "rsync", rsync__exclude: "target/"

  config.vm.provision :shell, inline: "echo 'installing java 8 from oracle and cassandra...'"
  config.vm.provision :chef_solo do |chef|
    chef.channel = "stable" 
    chef.version = "12.10.24"
    chef.cookbooks_path = "vagrant-provisioning/cookbooks"
    chef.add_recipe "java"
    chef.add_recipe "cassandra"
    chef.log_level = :info
  end

  config.vm.provision :shell, inline: "echo 'installing apache maven...'"
  config.vm.provision "shell", path: "vagrant-provisioning/shell/maven/maven.sh"

end
