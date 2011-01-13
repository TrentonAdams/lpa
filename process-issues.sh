#!/bin/bash
GITHUB_TOKEN=$(git config github.token)
# test url
#URL="http://github.com/api/v2/xml/issues/open/TrentonAdams/testing"
#LABEL_URL="http://github.com/api/v2/xml/issues/label/add/TrentonAdams/testing/"
OPEN_URL="https://github.com/api/v2/xml/issues/open/TrentonAdams/lpa"
LABEL_URL="https://github.com/api/v2/xml/issues/label/add/TrentonAdams/lpa/"

# WARNING, WARNING, WARNING major limitation in this script.  You cannot create multiple issues from the same file, as they get created as a single issue
FILES="$( egrep -rl 'new-issue{.+}' ./* | egrep -v 'process-issues|README')"
for i in $FILES; do 
  cp $i ${i}.newissue;
  eval $(egrep 'new-issue{.*}' ${i}.newissue | perl -e 'while (<>) { s/^.*new-issue{(.*)}.*$/$1/; print "$_\n"; }')
  issue=$(curl -F 'login=TrentonAdams' -F "token=$GITHUB_TOKEN" -F "title=$title" -F "body=$content" \
    $OPEN_URL 2>/dev/null | xpath -e '/issue/number/text()' 2>/dev/null)
  cat ${i}.newissue | perl -e 'while (<>) { s/new-issue{.*}/'"$title"' (issue-'$issue')/; print "$_"; }' > ${i}
  for l in $label; do 
    curl -F 'login=TrentonAdams' -F "token=$GITHUB_TOKEN" \
      ${LABEL_URL}${l}/$issue 2>/dev/null 1>/dev/null
  done;
  echo issue $issue created, and $i modified, please review

  rm ${i}.newissue;
done;

