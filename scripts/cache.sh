#!/bin/bash
cd /tmp
fileName=".scim_ctl_token_cache.txt"
if [ ! -e "$fileName" ]; then
  >"$fileName"
  chmod 700 "$fileName"
  echo "create cache at /tmp/$fileName"
  break
fi
