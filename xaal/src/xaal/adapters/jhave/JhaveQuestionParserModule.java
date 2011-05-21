/* 
This file is part of JHAVE -- Java Hosted Algorithm Visualization
Environment, developed by Tom Naps, David Furcy (both of the
University of Wisconsin - Oshkosh), Myles McNally (Alma College), and
numerous other contributors who are listed at the http://jhave.org
site

JHAVE is free software: you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free
Software Foundation, either version 3 of the License, or (at your
option) any later version.

JHAVE is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License
along with the JHAVE. If not, see:
<http://www.gnu.org/licenses/>.
*/

// Copyright 2008 Ville Karavirta
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package xaal.adapters.jhave;

import org.jdom.Element;
import org.xml.sax.Attributes;

import xaal.parser.ParserModule;

/**
 * @author vkaravir
 *
 */
public class JhaveQuestionParserModule extends ParserModule {
    public static final String QUESTION_COLLECTION_PROPERTY = "JhaveQuestionCollection";
    /**
     * 
     */
    public JhaveQuestionParserModule() {
        super();
    }

    public void startElementQuestions(String namespaceURI, String localName,
            String qName, Attributes atts) {
        Element questions = new Element("questions");
        setProperty(QUESTION_COLLECTION_PROPERTY, questions);
        push(questions);
    }

    public void endElementQuestions(String namespaceURI, String localName, String qName) {
        pop();
    }

    public void startElementQuestion(String namespaceURI, String localName,
            String qName, Attributes atts) {
        Element qElem = new Element("question");
        qElem.setAttribute("type", atts.getValue("type"));
        qElem.setAttribute("id", atts.getValue("id"));
        ((Element) peek()).addContent(qElem);
        push(qElem);
    }
    
    public void endElementQuestion(String namespaceURI, String localName, String qName) {
        pop();
    }
    
    public void startElementQuestionText(String namespaceURI, String localName,
            String qName, Attributes atts) {
        push(new StringBuffer());
    }
    
    public void endElementQuestionText(String namespaceURI, String localName, String qName) {
        StringBuffer sb = (StringBuffer) pop();
        Element q = (Element) getCurrentObject();
        Element qText = new Element("question_text");
        qText.addContent(sb.toString());
        q.addContent(qText);
    }
    
    public void startElementAnswerOption(String namespaceURI, String localName,
            String qName, Attributes atts) {
        Element answerOpt = new Element("answer_option");
        if (atts.getIndex("is_correct") != -1) {
            answerOpt.setAttribute("is_correct", atts.getValue("is_correct"));
        }
        ((Element) getCurrentObject()).addContent(answerOpt);
        push(answerOpt);
        push(new StringBuffer());
    }

    public void endElementAnswerOption(String namespaceURI, String localName, String qName) {
        StringBuffer sb = (StringBuffer) pop();
        Element elem = (Element) pop();
        elem.addContent(sb.toString());
    }

    public void characters(char[] text, int start, int length) {
        if (peek() != null && peek() instanceof StringBuffer) {
            StringBuffer sb = (StringBuffer) peek();
            for (int i = start; i < start + length; i++)
                sb.append(text[i]);
        }
    }
}
