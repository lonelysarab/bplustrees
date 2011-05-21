# ARG[0] will be file name (not including the .sho), ARG[1] and
# asterisk (probably not needed), ARG[2] ... ARG[the cntl-a separator]
# the string of numbers to sort followed by a cntl-a separator -- if
# they've chosen-user defined, the last arg is the menu choice

use strict;
use warnings;

my @numbers = (1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
my $i;
my $p;
my $temp;
my $swap_loc;
my $data = "";

# Gobble up data from text field
my $user_defined_data = "";

for ( $i = 2; $ARGV[$i] !~ m/\ca/; ++$i) {
    $user_defined_data = $user_defined_data . " " . $ARGV[ $i ];
}
$p = $i + 1;

if ($ARGV[$p] eq "Random") {
    for ($i = 9; $i > 0; $i--) {
	$swap_loc = int (rand ($i + 1));
	$temp = $numbers[$swap_loc];
	$numbers[$swap_loc] = $numbers[$i];
	$numbers[$i] = $temp;
    }

    for ($i = 0; $i < (8 + int (rand(2))); $i++) {
	$data = $data . "push" .  $numbers[$i] . " ";
    }

}
else {
    $data = $user_defined_data;
}

# print ("java exe.heapmyles.heapSort " . $ARGV[0] . ".sho $data");
system ("java exe.stacktest.StackTest " . $ARGV[0] . ".sho $data");



