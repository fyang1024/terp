/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tenx.terp.util;

import tech.tenx.terp.model.BestRateResponse;
import tech.tenx.terp.model.ExchangeCurrency;

public class OutputFormatter {

    private static String NEW_LINE = System.lineSeparator();

    public static String format(BestRateResponse response) {
        StringBuilder sb = new StringBuilder("BEST_RATES_BEGIN ");
        ExchangeCurrency srcCurrency = response.getRequest().getSrcCurrency();
        sb.append(srcCurrency)
                .append(" ").append(response.getRequest().getDestCurrency())
                .append(" ").append(response.getRate());
        boolean first = true;
        for (ExchangeCurrency exchangeCurrency : response.getPath().getSteps()) {
            sb.append(NEW_LINE).append(exchangeCurrency);
            if (!first) {
                sb.append(" ").append(response.getOriginalRates().get(srcCurrency).get(exchangeCurrency).getRate());
                srcCurrency = exchangeCurrency;
            }
            first = false;
        }
        if(response.getPath().isCircular()) {
            sb.append(NEW_LINE).append("...");
            sb.append(NEW_LINE).append(response.getRequest().getDestCurrency());
        }
        sb.append(NEW_LINE).append("BEST_RATES_END").append(NEW_LINE);
        return sb.toString();
    }
}
