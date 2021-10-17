/*
 * Copyright (c) 2020 Zavarov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package zav.discord.blanc.mc.argument._ast;

import de.monticore.prettyprint.IndentPrinter;
import zav.discord.blanc.command.parser.Argument;
import zav.mc.math.ArithmeticExpressionsPrettyPrinter;
import zav.mc.math.ArithmeticExpressionsValueCalculator;

import java.math.BigDecimal;
import java.util.Optional;

public class ASTExpressionArgument extends ASTExpressionArgumentTOP implements Argument {
    private static final ArithmeticExpressionsPrettyPrinter prettyPrinter = new ArithmeticExpressionsPrettyPrinter(new IndentPrinter());
    @Override
    public Optional<BigDecimal> asNumber() {
        return ArithmeticExpressionsValueCalculator.valueOf(getExpression());
    }

    @Override
    public Optional<String> asString() {
        return Optional.of(prettyPrinter.prettyprint(getExpression()));
    }
}