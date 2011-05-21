system("exe/topoalex/grapdriv " . @ARGV[0] . ".tmp " . @ARGV[0] . ".dat");
system("java exe.topoalex.TopoSort " . @ARGV[0] . ".dat " . @ARGV[0] . ".sho");
