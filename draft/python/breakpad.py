#!/usr/bin/python
import sys
import subprocess


print "debug so name is",sys.argv[1],;
subprocess.call('./dump_syms '+sys.argv[1]+' > '+sys.argv[1]+'.sym',shell=True);
symFile = sys.argv[1]+'.sym';
proc = subprocess.Popen(["head",symFile],stdout=subprocess.PIPE);
dirName='';
while True:
	line = proc.stdout.readline();
	if line!='':
		print line
		dirName = line.split(' ')[3];
		print ".sym path=" + dirName;
		break;
	else:
		break;

#generate head command random folder
subprocess.call('mkdir '+ dirName, shell=True);
#move "*.sym" file to random folder
subprocess.call('mv '+ symFile+' '+ dirName, shell=True);

#create "symbols" folder
symbolPath = "symbols";
subprocess.call('mkdir '+symbolPath, shell=True);

#create "symbols/lib***.so" folder
libPath = symbolPath + '/' + sys.argv[1];
subprocess.call('mkdir '+ libPath, shell=True);
#move random folder to "symbols" folder
subprocess.call('mv '+ dirName + ' ' + libPath, shell=True);
print "so lib path=" + libPath;

#move "minidump_stackwalk", "*.dmp file" to "symbols" folder
subprocess.call('mv ./minidump_stackwalk'+ ' ' + symbolPath, shell=True);
subprocess.call('mv ' + sys.argv[2] + ' ' + symbolPath, shell=True);

#execute command to generate result.txt
subprocess.call('./symbols/minidump_stackwalk '+ './symbols/'+ sys.argv[2]+" ./symbols/ > "+sys.argv[2]+".txt",shell=True);
#subprocess.call('rm -r '+dirName,shell=True);
#subprocess.call('head '+symFile,shell=True);
