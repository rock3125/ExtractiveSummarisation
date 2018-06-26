/*
 * Copyright (c) 2018 by Peter de Vocht
 *
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law.
 *
 */
package summarisation.parser;

import java.util.ArrayList;
import java.util.List;


public class Sentence {

    private List<Token> tokenList;

    public Sentence() {
        this.tokenList = new ArrayList<>();
    }

    public Sentence(List<Token> tokenList) {
        this.tokenList = tokenList;
    }

    // flatten a list of sentences into a list of tokens
    public static List<Token> flatten(List<Sentence> sentenceList) {
        List<Token> tokenList = new ArrayList<>();
        if (sentenceList != null) {
            for (Sentence sentence : sentenceList) {
                tokenList.addAll(sentence.getTokenList());
            }
        }
        return tokenList;
    }

    // fast pretty print
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Token token : tokenList) {
            sb.append(token.getText());
            sb.append(" ");
        }
        return sb.toString().trim();
    }


    public List<Token> getTokenList() {
        return tokenList;
    }

    public void setTokenList(List<Token> tokenList) {
        this.tokenList = tokenList;
    }

}

