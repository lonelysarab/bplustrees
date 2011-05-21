# ARG[0] will be file name (not including the .sho), ARG[1] and
# asterisk (probably not needed), ARG[2] the string to parse -- if
# they've embedded spaces, this will stretch over several args, ARG[3]
# is "\ca", ARG[4] the first priority, ARG[5] is "\ca", ARG[6] ...

#DEBUGGING
# 
# open( ERRFILE, ">/home/naps/junk/debug.txt") or die("Cannot open error file");
# print(ERRFILE "\ca", "\n");	# this shows how to make a cntl-a string in perl
# $i = 0;
# while( $i <= $#ARGV)
# {
# print(ERRFILE $ARGV[ $i ], "\n");
# $i = $i + 1;
# }
# close( ERRFILE ) or die("Cannot close error file");
#END DEBUGGING


# Form the string to execute
if ($ARGV[2] eq "QUIZMODE")
{
    $runstr = "./exe/parsing_lln/sbparsing " . $ARGV[ 0 ] . ".sho " . "A-B^C*(D-E)/F  1  1  1  1  2  2  2  2  3  3" . " " . "\n";
}
else
{
# The two lines below are all that is necessary if no embedded spaces
# $parsestr = $ARGV[2];
# $p = 4;
# But, if there are, the following loop gobbles the cntl-a's using the matching
# operator since the not equal test -- $ARGV[$i] != "\ca" -- didn't work
    $parsestr = "";
    for ( $i = 2; $ARGV[$i] !~ m/\ca/; ++$i) {
	$parsestr = $parsestr . $ARGV[ $i ];
    }
    $p = $i + 1;
    $runstr = "./exe/parsing_lln/sbparsing " . $ARGV[ 0 ] . ".sho " . $parsestr . " " . $ARGV[$p] . " " .  $ARGV[$p+2] . " " . $ARGV[$p+4] . " " .  $ARGV[$p+6] . " " .  $ARGV[$p+8] . " " .  $ARGV[$p+10] . " " .  $ARGV[$p+12] . " " .  $ARGV[$p+14] . " " .  $ARGV[$p+16] . " " .  $ARGV[$p+18] . " " . "\n";
}

# In the bash shell \( and \) must replace ( and ), so use subst operator
# First the left parens
$runstr =~ s/\(/\\\(/g;
# Then the right parens
$runstr =~ s/\)/\\\)/g;

#DEBUGGING
# 
# open( ERRFILE, ">/home/naps/junk/debug1.txt") or die("Cannot open error file");
# print(ERRFILE "\ca", "\n");	# this shows how to make a cntl-a string in perl
# print(ERRFILE $ARGV[0], "\n", $ARGV[1], "\n", "p = ", $p, "\n", $parsestr, "\n");
# $i = 3;
# while( $i <= $#ARGV)
# {
#     print(ERRFILE $i, " = ", $ARGV[ $i ], "\n");
#     $i = $i + 1;
# }
# 
# # print(ERRFILE "./exe/parsing_lln/sbparsing " . $ARGV[ 0 ] . ".sho " . $parsestr . " " . $ARGV[$p] . " " .  $ARGV[$p+2] . " " . $ARGV[$p+4] . " " .  $ARGV[$p+6] . " " .  $ARGV[$p+8] . " " .  $ARGV[$p+10] . " " .  $ARGV[$p+12] . " " .  $ARGV[$p+14] . " " .  $ARGV[$p+16] . " " .  $ARGV[$p+18] . " " . "\n");
# 
# print(ERRFILE $runstr);
# 
# close( ERRFILE ) or die("Cannot close error file");
# 
#END DEBUGGING

system($runstr);
