#!/usr/bin/perl 


if (($ARGV[0] eq "-h") || ($ARGV[0] eq "--h") || ($ARGV[0] eq "-help" )|| ($ARGV[0] eq "--help")|| (!defined($ARGV[2]))){
print "Param :
\t#Argument 1: input
\t#Argument 2: output
\t#Argument 3: bin size
\t#Argument 4: normalisation (NONE, KR, VC, VC_SRQT)
\t#Arg 5: chr size file
\t#Arg 6: path to juicebox_tools jar\n";
	die("\n");
}

my @chr;
my @observed;
my @expected;
my $jar = $ARGV[5];

open(F1,$ARGV[4]) || die "pblm fichier $ARGV[4]\n";
while(<F1>){
	my @tline = split("\t",$_);
	push(@chr, $tline[0]);
}
close F1;



for(my $i = 0; $i <= $#chr; $i++){
	my $outFile_obs = $ARGV[1].$chr[$i]."obs.txt";
	`java -jar $jar dump observed $ARGV[3] $ARGV[0] $chr[$i] $chr[$i] BP $ARGV[2]  $outFile_obs`;
	my $outFile_exp = $ARGV[1]."_chr".$chr[$i]."_expected.txt";
	`java -jar $jar  dump expected  $ARGV[3] $ARGV[0] $chr[$i] $chr[$i] BP $ARGV[2]  $outFile_exp`;
	push(@observed, $outFile_obs);
	push(@expected, $outFile_exp);
}

for(my $i = 0; $i <= $#expected; $i++){
	my $obsMExpected = $ARGV[1].$chr[$i].".txt";
	open(F1,$expected[$i]) || die "pblm fichier $expected[$i]\n";
	my @expecTab;
	while(<F1>){
		chomp($_);
		push(@expecTab,$_);
	}
	close (F1);
	
	open(RES2,">$obsMExpected") || die "pblm fichier $obsMExpected\n";
	open(F1,$observed[$i]) || die "pblm fichier $observed[$i]\n";
	while(<F1>){
		chomp($_);
		my @tline = split("\t",$_);
		my $dist = abs($tline[0]-$tline[1])/$ARGV[2];
		my $oMe =$tline[2] - $expecTab[$dist];
		print RES2 "$tline[0]\t$tline[1]\t$oMe\n";
	}
	close (F1);
	close(RES1);
	close(RES2)
	`rm $observed[$i] $expected[$i]`;
}
