{\rtf1\ansi\ansicpg1252\cocoartf1347\cocoasubrtf570
{\fonttbl\f0\fswiss\fcharset0 Helvetica;}
{\colortbl;\red255\green255\blue255;}
\paperw11900\paperh16840\margl1440\margr1440\vieww25400\viewh13580\viewkind0
\pard\tx566\tx1133\tx1700\tx2267\tx2834\tx3401\tx3968\tx4535\tx5102\tx5669\tx6236\tx6803\pardirnatural

\f0\fs24 \cf0 Geocon Service - Installation instructions:\
\
Configuring Elasticsearch, names and variables:\
1. In elasticsearch-x.x.x/config/elasticsearch.yml , set a name for your cluster (remember to uncomment the line deleting #)\
1.2. It's strongly recommended to customize the .yml config file. For example, if you're planning to use Elasticsearch on a single machine, you could disable redundancy (saving disk space). For further information, please refer to Elasticsearch documentation. \
2. In the root directory, customize the "config.properties" file. Feel free to customize the others fields :) NOTE: field "cluster_name" and the cluster name you set at 1 have to be the same\
\
Run elasticsearch cluster:\
PREREQUISITE for all users: you have JDK7 installed and JAVA_HOME as system variable set.\
\
*NIX Users:\
1. Using a terminal, enter in elasticsearch-x.x.x/bin/\
2. Type "./elasticsearch" without quotes and press return\
3. You're now running a fully working Elasticsearch cluster!\
4. Keep this window opened until you want to use the web service.\
\
Windows Users:\
1. Double click on elasticsearch.bat located in elasticsearch-x.x.x/bin/\
2. Done!\
\
Fill the Elasticsearch db with example data:\
1. With a terminal, enter in the root directory and type "java -jar geocon.jar fill" without quotes and press return.\
2. Follow on screen instructions.\
\
Run the Web service:\
1. With a terminal, enter in the root directory and type "java -jar geocon.jar" without quotes and press return.\
2. The Web service is now connected to Elasticsearch database.\
\
Try the Web service:\
(consigliare applicazione - interazione\'85)\
\
\
EXTRAS:\
\
Install Marvel plug-in for Elasticsearch.\
\pard\pardeftab720
\cf0 Marvel is a management and monitoring product for Elasticsearch. Marvel aggregates cluster wide statistics and events and offers a single interface to view and analyze them. Marvel is free for development use but does require a license to run in production.\
\
From the Elasticsearch home directory, run:\
bin/plugin -i elasticsearch/marvel/latest\
\
For other informations:\
\pard\tx566\tx1133\tx1700\tx2267\tx2834\tx3401\tx3968\tx4535\tx5102\tx5669\tx6236\tx6803\pardirnatural
\cf0 http://www.elastic.co/guide/en/marvel/current/_installation.html\
 }