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

package tech.tenx.terp;

import tech.tenx.terp.model.*;
import tech.tenx.terp.util.InputParser;
import tech.tenx.terp.util.OutputFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Console {

    private final static String BANNER = "\n" +
            "___  ___               __   ___  __  ___      __       ___  ___ \n" +
            " |  |__  |\\ | \\_/     |__) |__  /__`  |      |__)  /\\   |  |__  \n" +
            " |  |___ | \\| / \\     |__) |___ .__/  |      |  \\ /~~\\  |  |___ \n" +
            "                                                                 \n" +
            "\n" +
            "This program allows user to:\n" +
            "1. input timestamped crypto currency price updates on various exchanges\n" +
            "2. query the best exchange rate between any two currencies based on the\n" +
            "   latest prices\n" +
            "assuming same currency can be transferred between exchanges at no cost\n" +
            "\n" +
            "Price update input format:\n" +
            "<timestamp> <exchange> <source_currency> <destination_currency> <forward_factor> <backward_factor>\n" +
            "For example:\n" +
            "2017-11-01T09:42:23+00:00 KRAKEN BTC USD 1000.0 0.0009\n" +
            "signifies that a price update was received from Kraken on November 1, 2017 at 9:42:23 am\n" +
            "The update says that 1 BTC is worth 1000 USD and that 1 USD is worth 0.0009 BTC.\n" +
            "\n" +
            "Exchange rate requests will be:\n" +
            "EXCHANGE_RATE_REQUEST <source_exchange> <source_currency> <destination_exchange> <destination_currency>\n" +
            "This represents the question: What is the best exchange rate for converting <source_currency> on\n" +
            "<source_exchange> into <destination_currency> on <destination_exchange>, and what trades and transfers\n" +
            "need to be made to achieve that rate?\n" +
            "\n" +
            "For each exchange rate request, you will get a response as below:\n" +
            "BEST_RATES_BEGIN <source_exchange> <source_currency> <destination_exchange> <destination_currency> <rate>\n" +
            "<source_exchange> <source_currency>\n" +
            "<exchange1> <currency1> <conversion_rate from source_currency on source_exchange to currency1 on exchange1>\n" +
            "<exchange2> <currency2> <conversion_rate from currency1 on exchange1 to currency2 on exchange2>\n" +
            "...\n" +
            "<destination_exchange> <destination_currency>\n" +
            "BEST_RATES_END\n" +
            "\n" +
            "Type \"x\" or \"X\" to exit the program\n";

    public static void main(String[] args) throws IOException {
        if (args.length == 0 || !args[0].equals("-nb")) {
            System.out.println(BANNER);
        }
        System.out.println("Please send your instructions below:\n\n");

        ExchangeRateGraph graph = new ExchangeRateGraph();
        BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );
        String input;
        while ((input = reader.readLine()) != null) {
            if(input.toUpperCase().equals("X")) {
                System.out.println("Bye!");
                break;
            }
            if(InputParser.isExchangeRateRequest(input)){
                BestRateRequest request = InputParser.parseExchangeRateRequest(input);
                BestRateResponse response = graph.getBestRate(request);
                System.out.println(OutputFormatter.format(response));
            } else if(InputParser.isPriceUpdate(input)) {
                if (InputParser.isPriceUpdateValid(input)) {
                    ExchangeRate[] exchangeRates = InputParser.parsePriceUpdate(input);
                    graph.addOrUpdateExchangeRate(exchangeRates);
                    System.out.println("Ack! Price update received\n");
                } else {
                    System.out.println("Product of forward_factor and backward_factor is greater than one, input discarded\n");
                }
            } else {
                System.out.println("Input is not a price update or an exchange rate request, please try again\n");
            }
        }
    }

}
