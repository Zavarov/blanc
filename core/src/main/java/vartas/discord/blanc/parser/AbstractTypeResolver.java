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

package vartas.discord.blanc.parser;

import vartas.discord.blanc.ConfigurationModule;
import vartas.discord.blanc.Guild;
import vartas.discord.blanc.TextChannel;
import vartas.discord.blanc.command.Command;
import vartas.discord.blanc.prettyprint.ArgumentPrettyPrinter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The {@link IntermediateCommand} generated by the parser is only able to store the {@link Argument} as either a
 * {@link String} or {@link Number}. This class is able to use those arguments to create more complex data types.
 *
 * @see StringArgument
 * @see MentionArgument
 * @see ArithmeticArgument
 */
@Nonnull
public abstract class AbstractTypeResolver extends AbstractTypeResolverTOP {
    /**
     * The {@link Guild} associated with all resolved types.
     * <code>null</code> if the resolver doesn't support guilds.
     */
    @Nullable
    protected Guild guild;
    /**
     * The {@link TextChannel} associated with all resolved types.
     * <code>null</code> if the resolver doesn't support guilds.
     */
    @Nullable
    protected TextChannel textChannel;

    /**
     * Creates a fresh resolver instance for types that require a {@link Guild}.
     *
     * @param guild The {@link Guild} associated with the resolved types.
     * @param textChannel The {@link TextChannel} associated with the resolved types.
     */
    @Nonnull
    public AbstractTypeResolver(@Nullable Guild guild, @Nullable TextChannel textChannel) {
        this.guild = guild;
        this.textChannel = textChannel;
    }

    /**
     * Creates a fresh resolver instance for types that don't require a {@link Guild}.
     */
    @Nonnull
    public AbstractTypeResolver() {
        this(null, null);
    }

    /**
     * Attempts to transform the provided {@link Argument} into a {@link String}.
     * <p>
     * This transformation is only possible for instances of {@link StringArgument}, in which case its value is used.
     *
     * @param argument The {@link Argument} from which the {@link String} is extracted.
     * @return The {@link String} extracted from the {@link Argument}.
     * @throws NoSuchElementException If the {@link Argument} can't be resolved as a {@link String}.
     */
    @Override
    @Nonnull
    public String resolveString(@Nonnull Argument argument) throws NoSuchElementException {
        return new StringResolver().apply(argument).orElseThrow(
                () -> new NoSuchElementException(ArgumentPrettyPrinter.printPretty(argument))
        );
    }

    /**
     * Attempts to transform the provided {@link Argument} into a {@link LocalDate}.
     * <p>
     * This transformation is only possible for instances of {@link StringArgument}, in which case we assume the
     * value to represent a valid date. The date format is "yyyy-mm-dd".
     *
     * @param argument The {@link Argument} from which the {@link LocalDate} is extracted.
     * @return The {@link LocalDate} extracted from the {@link Argument}.
     * @throws NoSuchElementException If the {@link Argument} can't be resolved as a {@link LocalDate}.
     * @see LocalDateTime#parse(CharSequence)
     * @see DateTimeFormatter#ISO_LOCAL_DATE
     */
    @Override
    @Nonnull
    public LocalDate resolveLocalDate(@Nonnull Argument argument) throws NoSuchElementException {
        return new LocalDateResolver().apply(argument).orElseThrow(
                () -> new NoSuchElementException(ArgumentPrettyPrinter.printPretty(argument))
        );
    }

    /**
     * Attempts to transform the provided {@link Argument} into a {@link BigDecimal}.
     * <p>
     * Numbers are represented using arithmetic expressions which, in turn, are instances of {@link ArithmeticArgument}.
     * The parser already evaluates the expressions, therefore the {@link Argument} only contains the final result.
     *
     * @param argument The {@link Argument} from which the {@link BigDecimal} is extracted.
     * @return The {@link BigDecimal} extracted from the {@link Argument}.
     * @throws NoSuchElementException If the {@link Argument} can't be resolved as a {@link BigDecimal}.
     */
    @Override
    @Nonnull
    public BigDecimal resolveBigDecimal(@Nonnull Argument argument) throws NoSuchElementException {
        return new BigDecimalResolver().apply(argument).orElseThrow(
                () -> new NoSuchElementException(ArgumentPrettyPrinter.printPretty(argument))
        );
    }

    /**
     * Attempts to transform the provided {@link Argument} into a {@link ConfigurationModule}.
     * <p>
     * A module is identified by finding the enum value with the matching name. The check is case-insensitive.
     *
     * @param argument The {@link Argument} from which the {@link ConfigurationModule} is extracted.
     * @return The {@link ConfigurationModule} extracted from the {@link Argument}.
     * @throws NoSuchElementException If the {@link Argument} can't be resolved as a {@link ConfigurationModule}.
     * @see ConfigurationModule
     */
    @Override
    @Nonnull
    public ConfigurationModule resolveConfigurationModule(@Nonnull Argument argument) throws NoSuchElementException {
        return new ConfigurationModuleResolver().apply(argument).orElseThrow(
                () -> new NoSuchElementException(ArgumentPrettyPrinter.printPretty(argument))
        );
    }

    /**
     * Attempts to transform the provided {@link Argument} into a {@link ChronoUnit}.
     * <p>
     * The time unit is identified by finding the enum value with the matching name. The check is case-insensitive.
     *
     * @param argument The {@link Argument} from which the {@link ChronoUnit} is extracted.
     * @return The {@link ChronoUnit} extracted from the {@link Argument}.
     * @throws NoSuchElementException If the {@link Argument} can't be resolved as a {@link ChronoUnit}.
     * @see ChronoUnit
     */
    @Override
    @Nonnull
    public ChronoUnit resolveChronoUnit(@Nonnull Argument argument) throws NoSuchElementException {
        return new ChronoUnitResolver().apply(argument).orElseThrow(
                () -> new NoSuchElementException(ArgumentPrettyPrinter.printPretty(argument))
        );
    }

    /**
     * Attempts to transform an {@link Argument} at an specified index. In case the provided list is smaller than the
     * index, {@link Optional#empty()} is return. Otherwise an {@link Optional} containing the resolved argument at the
     * specific position is returned. This method is used for commands, where the program has a default state that is
     * executed if no {@link Argument} is provided.
     * @param arguments A {@link List} of all arguments of a  received {@link Command}.
     * @param index The index of the {@link Argument} that is resolved. May be larger than the size of the provided
     *              {@link List}.
     * @param transformer The {@link Function} for transforming the {@link Argument} at the specified index into a new
     *                    type.
     * @param <T> The generic type the {@link Argument} is resolved into.
     * @return An {@link Optional} containing the resolved type. If the index is outside the range of the provided
     *         {@link List}, {@link Optional#empty()} is returned.
     * @throws NoSuchElementException If the {@link Argument} can't be resolved. Note that this exception is only thrown
     *                                if the {@link List} contains an {@link Argument} at the specified index.
     */
    public <T> Optional<T> resolveOptional(@Nonnull List<? extends Argument> arguments, int index, @Nonnull Function<? super Argument, T> transformer) throws NoSuchElementException {
        return index < arguments.size() ? Optional.of(transformer.apply(arguments.get(index))) : Optional.empty();
    }


    /**
     * Attempts to transform an list of arguments at once. This method is used for commands that may require an
     * arbitrary amount of arguments. In that case, multiple arguments are grouped together to match the {@link Command}
     * signature.
     * @param arguments A (sub-)list of all arguments of a  received {@link Command}.
     * @param transformer The {@link Function} for transforming all arguments of the provided {@link List} into the
     *                    specified type.
     * @param <T> The generic type the {@link Argument} is resolved into.
     * @return An {@link List} containing all resolved types, in order in which they appear.
     * @throws NoSuchElementException If at least one {@link Argument} can't be resolved..
     */
    public <T> List<T> resolveMany(@Nonnull List<? extends Argument> arguments, @Nonnull Function<? super Argument, T> transformer) throws NoSuchElementException {
        return arguments.stream().map(transformer).collect(Collectors.toList());
    }

    /**
     * Part of the visitor pattern to grant access to the explicit implementation of the individual types.
     * @return The current instance.
     */
    @Override
    public AbstractTypeResolver getRealThis() {
        return this;
    }
}