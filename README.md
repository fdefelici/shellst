# SHELLST [![Build Status](https://travis-ci.org/fdefelici/travis-maven-try.svg?branch=master)](https://travis-ci.org/fdefelici/travis-maven-try)
Shellst allow you to access your server shell by rest api. Useful in case you can't access remotly your server by ssh.

Both Windows (cmd) and Unix (sh) shell are supported. 

# Running shellst
> java [OPTIONS] -jar shellst-\<version\>.jar 

Available options:
* *-Dport*: Specify listening port (default: 4567)
* *-Dtoken*: Specify a token to access api (default: not set)

NOTE: Options can be passed also as environment variables

# API

## HELO
To check if application is working
> curl "http://<IP>:<PORT>/"

## EXEC SHELL COMMAND 
Run a shell command on the host machine (like ssh command)
> curl -X POST "http://localhost:4567/shell/exec" --data-urlencode "cmd=mkdir /tmp/mydir"

## COPY 
Run a copy file from client to server. (like scp command)
> curl "http://<IP>:<PORT>/shell/copy" -F "file=@local/file/path/myfile.example" -F "path=/dest/path/filename.example"

NOTE: like scp destination path must exists
