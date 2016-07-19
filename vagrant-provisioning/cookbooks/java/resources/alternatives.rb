actions :set, :unset

attribute :java_location, kind_of: String, default: nil
attribute :bin_cmds, kind_of: Array, default: nil
attribute :default, equal_to: [true, false], default: true
attribute :priority, kind_of: Integer, default: 1061
attribute :reset_alternatives, equal_to: [true, false], default: true

def initialize(*args)
  super
  @action = :set
end
