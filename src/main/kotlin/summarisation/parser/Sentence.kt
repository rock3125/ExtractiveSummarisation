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
package summarisation.parser

import java.util.ArrayList


class Sentence {

    var tokenList = ArrayList<Token>()

    constructor() {
    }

    constructor(tokenList: List<Token>) {
        this.tokenList.addAll(tokenList)
    }

    // fast pretty print
    override fun toString(): String {
        val sb = StringBuilder()
        for (token in tokenList) {
            sb.append(token.text)
            sb.append(" ")
        }
        return sb.toString().trim()
    }

}

