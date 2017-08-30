/*
 * #%L
 * Eureka! Clinical User Webapp
 * %%
 * Copyright (C) 2016 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.eurekaclinical.rhsit.taglib.templates;

import java.util.Hashtable;
import java.util.Stack;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import static javax.servlet.jsp.tagext.Tag.SKIP_BODY;
import static javax.servlet.jsp.tagext.TagSupport.findAncestorWithClass;

/**
 *
 * @author sagrava
 *
 * Extension of Simple Tag Library from sun examples. Allows capability to
 * specify content in between tags rather than hardcoding in an attribute from
 * the PutTag.
 *
 * Usage: <pre>{@code <template:content name="some name">
 * 			content goes here
 * 		  </template:content>}</pre>
 *
 */
public class ContentTag extends BodyTagSupport {

    private String name;

    public void setName(String s) {
        name = s;
    }

    @Override
    public int doAfterBody() throws JspException {
        BodyContent body = getBodyContent();
        if (body == null) {
            throw new JspException("ContentTag.doStartTag(): "
                    + "No JSP body present under template:content");
        }

        String htmlBody = body.getString();
        InsertTag parent
                = (InsertTag) findAncestorWithClass(this, org.eurekaclinical.rhsit.taglib.templates.InsertTag.class);
        if (parent == null) {
            throw new JspException("ContentTag.doStartTag(): "
                    + "No InsertTag ancestor");
        }

        Stack template_stack = parent.getStack();

        if (template_stack == null) {
            throw new JspException("ContentTag: no template stack");
        }

        Hashtable params = (Hashtable) template_stack.peek();

        if (params == null) {
            throw new JspException("ContentTag: no hashtable");
        }

        params.put(name, new PageParameter(htmlBody, "true"));

        return (SKIP_BODY);
    }

}
