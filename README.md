# SHELLST [![Build Status](https://travis-ci.org/fdefelici/shellst.svg?branch=master)](https://travis-ci.org/fdefelici/shellst)
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

Curl Example:
> curl "http://localhost:4567/"

## EXEC SHELL COMMAND 
Run a shell command on the host machine (like ssh command)

Curl Example:
> curl -X POST "http://localhost:4567/shell/exec" --data-urlencode "cmd=mkdir /tmp/mydir"

## COPY 
Run a copy file from client to server. (like scp command)

Curl Example:
> curl "http://localhost:4567/shell/copy" -F "file=@local/file/path/myfile.example" -F "path=/dest/path/filename.example"

NOTE: like scp destination path must exists
