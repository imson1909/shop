package net.sixik.sdmshop;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshop.config.ShopConfig;
import net.sixik.sdmshop.currencies.SDMCoin;
import net.sixik.sdmshop.network.async.AsyncServerTasks;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.utils.ShopUtils;

public class SDMShopCommands {
   public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_(
                                                "sdmshop"
                                             )
                                             .then(
                                                ((LiteralArgumentBuilder)Commands.m_82127_("balance")
                                                      .executes(
                                                         context -> balance(
                                                            (CommandSourceStack)context.getSource(), ((CommandSourceStack)context.getSource()).m_81375_()
                                                         )
                                                      ))
                                                   .then(
                                                      ((RequiredArgumentBuilder)Commands.m_82129_("player", GameProfileArgument.m_94584_())
                                                            .requires(source -> source.m_6761_(2)))
                                                         .executes(
                                                            context -> balance(
                                                               (CommandSourceStack)context.getSource(), ((CommandSourceStack)context.getSource()).m_81375_()
                                                            )
                                                         )
                                                   )
                                             ))
                                          .then(
                                             Commands.m_82127_("pay")
                                                .then(
                                                   Commands.m_82129_("player", EntityArgument.m_91466_())
                                                      .then(
                                                         Commands.m_82129_("money", DoubleArgumentType.doubleArg(1.0))
                                                            .executes(
                                                               context -> pay(
                                                                  (CommandSourceStack)context.getSource(),
                                                                  ((CommandSourceStack)context.getSource()).m_81375_(),
                                                                  EntityArgument.m_91474_(context, "player"),
                                                                  DoubleArgumentType.getDouble(context, "money")
                                                               )
                                                            )
                                                      )
                                                )
                                          ))
                                       .then(
                                          ((LiteralArgumentBuilder)Commands.m_82127_("set").requires(source -> source.m_6761_(2)))
                                             .then(
                                                Commands.m_82129_("player", EntityArgument.m_91470_())
                                                   .then(
                                                      Commands.m_82129_("money", DoubleArgumentType.doubleArg(0.0))
                                                         .executes(
                                                            context -> set(
                                                               (CommandSourceStack)context.getSource(),
                                                               EntityArgument.m_91477_(context, "player"),
                                                               DoubleArgumentType.getDouble(context, "money")
                                                            )
                                                         )
                                                   )
                                             )
                                       ))
                                    .then(
                                       ((LiteralArgumentBuilder)Commands.m_82127_("set_balance").requires(source -> source.m_6761_(2)))
                                          .then(
                                             Commands.m_82129_("player", EntityArgument.m_91470_())
                                                .then(
                                                   Commands.m_82129_("money", DoubleArgumentType.doubleArg(0.0))
                                                      .executes(
                                                         context -> set(
                                                            (CommandSourceStack)context.getSource(),
                                                            EntityArgument.m_91477_(context, "player"),
                                                            DoubleArgumentType.getDouble(context, "money")
                                                         )
                                                      )
                                                )
                                          )
                                    ))
                                 .then(
                                    ((LiteralArgumentBuilder)Commands.m_82127_("add").requires(source -> source.m_6761_(2)))
                                       .then(
                                          Commands.m_82129_("player", EntityArgument.m_91470_())
                                             .then(
                                                Commands.m_82129_("money", DoubleArgumentType.doubleArg())
                                                   .executes(
                                                      context -> add(
                                                         (CommandSourceStack)context.getSource(),
                                                         EntityArgument.m_91477_(context, "player"),
                                                         DoubleArgumentType.getDouble(context, "money")
                                                      )
                                                   )
                                             )
                                       )
                                 ))
                              .then(
                                 ((LiteralArgumentBuilder)Commands.m_82127_("add_balance").requires(source -> source.m_6761_(2)))
                                    .then(
                                       Commands.m_82129_("player", EntityArgument.m_91470_())
                                          .then(
                                             Commands.m_82129_("money", DoubleArgumentType.doubleArg())
                                                .executes(
                                                   context -> add(
                                                      (CommandSourceStack)context.getSource(),
                                                      EntityArgument.m_91477_(context, "player"),
                                                      DoubleArgumentType.getDouble(context, "money")
                                                   )
                                                )
                                          )
                                    )
                              ))
                           .then(
                              ((LiteralArgumentBuilder)Commands.m_82127_("edit_mode").requires(source -> source.m_6761_(2)))
                                 .executes(context -> editMode((CommandSourceStack)context.getSource()))
                           ))
                        .then(
                           ((LiteralArgumentBuilder)Commands.m_82127_("create_shop").requires(source -> source.m_6761_(2)))
                              .then(
                                 Commands.m_82129_("id", StringArgumentType.string())
                                    .executes(context -> createShop((CommandSourceStack)context.getSource(), StringArgumentType.getString(context, "id")))
                              )
                        ))
                     .then(
                        ((LiteralArgumentBuilder)Commands.m_82127_("delete_shop").requires(source -> source.m_6761_(2)))
                           .then(
                              Commands.m_82129_("shop_id", StringArgumentType.greedyString())
                                 .suggests(
                                    (commandContext, builder) -> SharedSuggestionProvider.m_82981_(SDMShopServer.Instance().getAllShopIDs().stream(), builder)
                                 )
                                 .executes(context -> removeShop((CommandSourceStack)context.getSource(), StringArgumentType.getString(context, "shop_id")))
                           )
                     ))
                  .then(
                     ((LiteralArgumentBuilder)Commands.m_82127_("reload_config_server").requires(source -> source.m_6761_(2)))
                        .executes(context -> reloadServerConfig((CommandSourceStack)context.getSource()))
                  ))
               .then(
                  ((LiteralArgumentBuilder)Commands.m_82127_("open_shop").requires(source -> source.m_6761_(2)))
                     .then(
                        ((RequiredArgumentBuilder)Commands.m_82129_("player", EntityArgument.m_91470_())
                              .executes(context -> openShop((CommandSourceStack)context.getSource(), StringArgumentType.getString(context, "shop_id"), null)))
                           .then(
                              Commands.m_82129_("shop_id", StringArgumentType.greedyString())
                                 .suggests(
                                    (commandContext, builder) -> SharedSuggestionProvider.m_82981_(SDMShopServer.Instance().getAllShopIDs().stream(), builder)
                                 )
                                 .executes(
                                    context -> openShop(
                                       (CommandSourceStack)context.getSource(),
                                       StringArgumentType.getString(context, "shop_id"),
                                       EntityArgument.m_91477_(context, "player")
                                    )
                                 )
                           )
                     )
               ))
            .then(Commands.m_82127_("help").executes(context -> printHelpInformation((CommandSourceStack)context.getSource())))
      );
   }

   private static int reloadServerConfig(CommandSourceStack source) {
      ShopConfig.reload();
      source.m_288197_(() -> Component.m_237113_("Config reloaded!").m_130940_(ChatFormatting.GREEN), false);
      return 1;
   }

   private static int openShop(CommandSourceStack source, String shopId, Collection<ServerPlayer> profiles) throws CommandSyntaxException {
      if (!SDMShopServer.Instance().exists(shopId)) {
         throw new CommandSyntaxException(
            CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect(), new LiteralMessage("Shop with ID " + shopId + " does not exist")
         );
      }

      if (profiles != null) {
         for (ServerPlayer profile : profiles) {
            AsyncServerTasks.openShopOrCache(profile, SDMShopServer.parseLocation(shopId));
         }
      } else if (source.m_230896_() != null) {
         AsyncServerTasks.openShopOrCache(source.m_230896_(), SDMShopServer.parseLocation(shopId));
      }

      return 1;
   }

   private static int createShop(CommandSourceStack source, String shopId) {
      if (source.m_230896_() == null) {
         return 0;
      } else if (SDMShopServer.Instance().exists(shopId)) {
         source.m_81352_(Component.m_237113_("Shop ").m_130946_(shopId).m_130946_(" already exists"));
         return 0;
      } else {
         SDMShopServer.Instance().createShop(shopId);
         source.m_288197_(() -> Component.m_237113_("Shop ").m_7220_(Component.m_237113_(shopId).m_130940_(ChatFormatting.GOLD)).m_130946_(" created!"), false);
         return 1;
      }
   }

   private static int removeShop(CommandSourceStack source, String shopId) {
      if (source.m_230896_() == null) {
         return 0;
      } else if (SDMShopServer.Instance().removeShop(shopId)) {
         source.m_288197_(() -> Component.m_237113_("Shop ").m_7220_(Component.m_237113_(shopId).m_130940_(ChatFormatting.GOLD)).m_130946_(" removed!"), false);
         return 1;
      } else {
         return 0;
      }
   }

   private static int editMode(CommandSourceStack source) {
      if (source.m_230896_() != null) {
         ShopUtils.changeEditMode(source.m_230896_(), !ShopUtils.isEditMode(source.m_230896_()));
         source.m_288197_(() -> Component.m_237113_("Edit mode is " + ShopUtils.isEditMode(source.m_230896_())), false);
      }

      return 1;
   }

   private static int balance(CommandSourceStack source, ServerPlayer profiles) {
      source.m_288197_(() -> Component.m_237113_(ShopUtils.moneyToString(profiles)), false);
      return 1;
   }

   private static int pay(CommandSourceStack source, ServerPlayer from, ServerPlayer to, double money) {
      if (from.m_20148_().equals(to.m_20148_())) {
         source.m_81352_(Component.m_237113_("You can't send money to yourself"));
         return 1;
      } else if (ShopUtils.getMoney(from) >= money) {
         ShopUtils.setMoney(from, ShopUtils.getMoney(from) - money);
         ShopUtils.setMoney(to, ShopUtils.getMoney(to) + money);
         source.m_288197_(() -> Component.m_237113_("Money sended !"), false);
         return 0;
      } else {
         source.m_81352_(Component.m_237113_("Not enough money"));
         return 1;
      }
   }

   private static int set(CommandSourceStack source, Collection<ServerPlayer> players, double money) {
      for (ServerPlayer player : players) {
         ShopUtils.setMoney(player, money);
         source.m_288197_(() -> Component.m_237113_(player.m_6302_() + ": ").m_130946_(ShopUtils.moneyToString(player)), false);
      }

      return players.size();
   }

   private static int add(CommandSourceStack source, Collection<ServerPlayer> players, double money) {
      if (money == 0.0) {
         return 0;
      }

      for (ServerPlayer player : players) {
         source.m_288197_(() -> {
            String var10000 = player.m_6302_();
            return Component.m_237113_(var10000 + (money > 0.0 ? ": +" : ": -")).m_130946_(ShopUtils.moneyToString(Math.abs(money), SDMCoin.getId()));
         }, false);
         ShopUtils.addMoney(player, money);
      }

      return players.size();
   }

   public static void registerCommands(
      CommandDispatcher<CommandSourceStack> commandSourceStackCommandDispatcher, CommandBuildContext commandBuildContext, CommandSelection commandSelection
   ) {
      registerCommands(commandSourceStackCommandDispatcher);
   }

   private static int printHelpInformation(CommandSourceStack source) {
      boolean isAdmin = source.m_6761_(2);
      source.m_288197_(
         () -> Component.m_237113_("=== ")
            .m_7220_(Component.m_237113_("SDM Shop | Help").m_130940_(ChatFormatting.GOLD))
            .m_130946_(" ===")
            .m_130940_(ChatFormatting.YELLOW),
         false
      );
      sendHelpLine(source, "/sdmshop help", "Shows this help message");
      sendHelpLine(source, "/sdmshop balance", "Check your balance");
      sendHelpLine(source, "/sdmshop pay <player> <amount>", "Send money to another player");
      if (isAdmin) {
         source.m_288197_(
            () -> Component.m_237113_("\n=== ")
               .m_7220_(Component.m_237113_("Administration").m_130940_(ChatFormatting.RED))
               .m_130946_(" ===")
               .m_130940_(ChatFormatting.DARK_RED),
            false
         );
         sendHelpLine(source, "/sdmshop balance <player>", "Check the balance of a specific player");
         sendHelpLine(source, "/sdmshop set_balance | set <player> <amount>", "Set the exact balance of a player");
         sendHelpLine(source, "/sdmshop add_balance | add <player> <amount>", "Add or remove (negative value) money");
         sendHelpLine(source, "/sdmshop edit_mode", "Toggle edit mode");
         sendHelpLine(source, "/sdmshop create_shop <id>", "Create a new shop");
         sendHelpLine(source, "/sdmshop delete_shop <id>", "Delete an existing shop");
         sendHelpLine(source, "/sdmshop open_shop <player> <id>", "Force open a shop for a player");
         sendHelpLine(source, "/sdmshop reload_config_server", "Reload the server configuration");
      }

      source.m_288197_(() -> Component.m_237113_("=========================").m_130940_(ChatFormatting.YELLOW), false);
      return 1;
   }

   private static void sendHelpLine(CommandSourceStack source, String command, String description) {
      source.m_288197_(
         () -> Component.m_237119_()
            .m_7220_(Component.m_237113_(command).m_130940_(ChatFormatting.AQUA))
            .m_7220_(Component.m_237113_(" - ").m_130940_(ChatFormatting.DARK_GRAY))
            .m_7220_(Component.m_237113_(description).m_130940_(ChatFormatting.GRAY)),
         false
      );
   }
}
