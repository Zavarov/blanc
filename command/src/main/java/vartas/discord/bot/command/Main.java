package vartas.discord.bot.command;

import com.google.common.base.Preconditions;
import de.monticore.ModelingLanguage;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.IterablePath;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import org.apache.commons.io.FileUtils;
import vartas.discord.bot.command.command.CommandHelper;
import vartas.discord.bot.command.command._ast.ASTCommandArtifact;
import vartas.discord.bot.command.command._cocos.CommandCoCoChecker;
import vartas.discord.bot.command.command._cocos.CommandCoCos;
import vartas.discord.bot.command.command._symboltable.CommandLanguage;
import vartas.discord.bot.command.command.generator.CommandBuilderGenerator;
import vartas.discord.bot.command.command.generator.CommandGenerator;
import vartas.discord.bot.command.command.generator.CommandGeneratorHelper;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/*
 * Copyright (C) 2019 Zavarov
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
public class Main {
    private static final String TEMPLATE_EXTENSION = "ftl";

    private static final String TARGET_EXTENSION = "java";

    private static final GlobalExtensionManagement GLEX = new GlobalExtensionManagement();

    private static final GeneratorSetup SETUP = new GeneratorSetup();

    private static final GeneratorEngine GENERATOR = new GeneratorEngine(SETUP);

    static{
        GLEX.defineGlobalVar("helper", new CommandGeneratorHelper());

        SETUP.setGlex(GLEX);
    }

    /**
     * Generates the models specified in the arguments.
     * There are at least five arguments required for this method:
     * <ul>
     *     <li>The path to the models</li>
     *     <li>The path to the templates</li>
     *     <li>The directory of the handwritten sources</li>
     *     <li>The target directory of the generated sources</li>
     *     <li>The package name of the command builder</li>
     * </ul>
     *
     * @param args the arguments for generating the commands.
     */
    public static void main(String[] args){
        Preconditions.checkArgument(args.length >= 4, "Please provide at least 5 arguments.");
        Preconditions.checkArgument(new File(args[0]).exists(), args[0]+": Please make sure that the model file exists");
        Preconditions.checkArgument(new File(args[1]).exists(), args[1]+": Please make sure that the template file exists");
        Preconditions.checkArgument(new File(args[2]).exists(), args[2]+": Please make sure that the source file exists");

        File modelFolder = new File(args[0]).getAbsoluteFile();
        File templateFolder = new File(args[1]).getAbsoluteFile();
        File targetDirectory = new File(args[2]).getAbsoluteFile();
        File outputDirectory = new File(args[3]).getAbsoluteFile();
        String packageName = args[4];

        IterablePath templatePath = IterablePath.from(templateFolder, TEMPLATE_EXTENSION);
        IterablePath targetPath = IterablePath.from(targetDirectory, TARGET_EXTENSION);
        SETUP.setAdditionalTemplatePaths(templatePath.getPaths().stream().map(Path::toFile).collect(Collectors.toList()));
        SETUP.setOutputDirectory(outputDirectory);

        GlobalScope scope = createGlobalScope();

        List<ASTCommandArtifact> models = FileUtils.listFiles(modelFolder, new String[]{ CommandLanguage.COMMAND_FILE_ENDING}, false)
                .stream()
                .map(file -> CommandHelper.parse(scope, file.getPath()))
                .collect(Collectors.toList());

        CommandCoCoChecker checker = CommandCoCos.getCheckerForAllCoCos();

        models.forEach(checker::checkAll);

        generateCommands(models, scope, targetPath);
        generateCommandBuilder(models, scope, packageName);
    }

    private static void generateCommandBuilder(List<ASTCommandArtifact> models, GlobalScope scope, String packageName){
        CommandBuilderGenerator.generate(models, GENERATOR, packageName);
    }


    private static void generateCommands(Collection<ASTCommandArtifact> models, GlobalScope scope, IterablePath targetPath){
        models.forEach(model -> generateCommands(model, scope, targetPath));
    }

    private static void generateCommands(ASTCommandArtifact ast, GlobalScope scope, IterablePath targetPath){
        CommandGenerator.generate(ast, GENERATOR, targetPath);
    }


    private static GlobalScope createGlobalScope(){
        ModelPath path = new ModelPath(Paths.get(""));
        ModelingLanguage language = new CommandLanguage();
        return new GlobalScope(path, language);
    }
}