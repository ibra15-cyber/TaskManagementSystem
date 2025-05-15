   # Use a Tomcat base image
FROM tomcat:9.0-jdk17

    # Remove the default Tomcat app
RUN rm -rf /usr/local/tomcat/webapps/ROOT

    # Copy your WAR file to Tomcat's webapps directory \
COPY taskmanager.war /usr/local/tomcat/webapps/ROOT.war

    # Expose Tomcat's default port (8080)
EXPOSE 8080