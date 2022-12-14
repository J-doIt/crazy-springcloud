FROM dockerlocal:5000/java:0.0.2
ADD /cloud-zuul-1.0-SNAPSHOT.jar //
ADD /cloud-zuul.sh /usr/bin/
RUN chmod +x /usr/bin/cloud-zuul.sh
CMD ["cloud-zuul.sh"]
