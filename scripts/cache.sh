#!/bin/bash
cd /tmp
if [ ! -e .token.txt ]; then
  >.token.txt
  chmod 700 /tmp/.token.txt
  echo 'cache created at /tmp/.token.txt'
fi
