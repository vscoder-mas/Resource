#!/usr/bin/python
# -*- coding:utf-8 -*-

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

#生成head命令显示出来的长串文件夹	
subprocess.call('mkdir '+ dirName, shell=True);
#将生成的.sym文件放到长串文件夹中
subprocess.call('mv '+ symFile+' '+ dirName, shell=True);

#新建symbols文件夹
symbolPath = "symbols";
subprocess.call('mkdir '+symbolPath, shell=True);

#生成symbols/lib***.so文件夹
libPath = symbolPath + '/' + sys.argv[1];
subprocess.call('mkdir '+ libPath, shell=True);
#将长串文件夹mv到symbols文件夹下
subprocess.call('mv '+ dirName + ' ' + libPath, shell=True);
print "so lib path=" + libPath;

#将minidump_stackwalk, *.dmp文件mv到symbols文件夹下
subprocess.call('mv ./minidump_stackwalk'+ ' ' + symbolPath, shell=True);
subprocess.call('mv ' + sys.argv[2] + ' ' + symbolPath, shell=True);

#执行生成result.txt命令
subprocess.call('./symbols/minidump_stackwalk '+ './symbols/'+ sys.argv[2]+" ./symbols/ > "+sys.argv[2]+".txt",shell=True);
#subprocess.call('rm -r '+dirName,shell=True);
#subprocess.call('head '+symFile,shell=True);
