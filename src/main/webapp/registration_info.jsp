<%--
  #%L
  Eureka! Clinical User Webapp
  %%
  Copyright (C) 2016 Emory University
  %%
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
       http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  #L%
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/tlds/template.tld" prefix="template" %>

<template:insert template="/templates/eureka_main.jsp">
    <template:content name="content">
        <c:choose>
            <c:when test="${requestScope.error}">
                <h1>Registration not verified!</h1>
                <div class="alert alert-danger" role="alert">
                    <strong>Verification failed!</strong> ${error}
                </div>
            </c:when>
            <c:otherwise>
                <h1>Registration verified!</h1>
                <div class="alert alert-success" role="alert">
                    <string>Thank you for verifying your registration!</strong> Your account will be activated within the next 3 business days. You will be notified by e-mail when activation has occurred.
                </div>
            </c:otherwise>
        </c:choose>
    </template:content>
</template:insert>


