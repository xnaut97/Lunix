package com.github.tezvn.lunix;

import com.comphenix.packetwrapper.wrappers.play.clientbound.WrapperPlayServerBlockBreakAnimation;
import com.comphenix.packetwrapper.wrappers.play.clientbound.WrapperPlayServerEntityEquipment;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.particles.ParticleDisplay;
import com.cryptomorin.xseries.particles.XParticle;
import com.github.tezvn.lunix.chat.v1.MessageUtils;
import com.github.tezvn.lunix.location.CircleGenerator;
import com.github.tezvn.lunix.menu.Menu;
import com.github.tezvn.lunix.transaction.PlayerTransaction;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.FontImages.PlayerCustomHudWrapper;
import dev.lone.itemsadder.api.FontImages.PlayerHudsHolderWrapper;
import dev.lone.itemsadder.api.ItemsAdder;
import lombok.Getter;
import net.minecraft.network.protocol.game.PacketPlayOutSetSlot;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;


@Getter
public class Lunix extends JavaPlugin implements Listener {

    private boolean registered;

    private Map<Player, Integer> jumps = Maps.newHashMap();
    private ProtocolManager protocol;
    private BukkitRunnable runnable;

    private WorldGeneration worldGeneration;

    @Override
    public void onEnable() {
        Menu.register(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        initProtocol();
        initRunnable();
    }

    private void generateTerrain(World world) {
        int width = 512;
        int length = 512;

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < length; z++) {
                int blockX = x - width / 2;
                int blockZ = z - length / 2;
                double noiseValue = OpenSimplex2.noise3_Fallback(12345, (float) (blockX * 0.01), 1, (float) (blockZ * 0.01)); // Điều chỉnh tần số theo nhu cầu
                int groundHeight = (int) (noiseValue * 32) + 64; // Điều chỉnh độ cao theo nhu cầu

                for (int y = 0; y < groundHeight; y++) {
                    world.getBlockAt(blockX, y, blockZ).setType(Material.STONE);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        Menu.unregister();
        if (this.runnable != null) this.runnable.cancel();
    }

    @Override
    public void onLoad() {
        this.protocol = ProtocolLibrary.getProtocolManager();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!(entity instanceof Player)) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        new PlayerTransaction(this).setPlayer(event.getPlayer()).setOpposite((Player) entity).start();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer().getFallDistance() > 19) {
            event.getPlayer().setAllowFlight(false);
            return;
        }
        if (event.getPlayer().isOnGround()) {
            // Người chơi đang đứng trên mặt đất, reset tốc độ Y để cho phép nhảy.
            event.getPlayer().setAllowFlight(true);
            this.jumps.put(event.getPlayer(), 30);
        }
    }

//    @EventHandler
//    public void onPlayerStatistic(PlayerStatisticIncrementEvent event) {
//        Player player = event.getPlayer();
//        Statistic statistic = event.getStatistic();
//        System.out.println("Prev: " + event.getPreviousValue());
//        System.out.println("Next: " + event.getNewValue());
//        if(statistic == Statistic.JUMP) {
//            int jumps = this.jumps.getOrDefault(event.getPlayer(), 30);
//            Location location = player.getLocation();
//            if(location.getBlock().getType() == Material.AIR) {
//                this.jumps.put(event.getPlayer(), jumps - 1);
//                player.setVelocity(player.getVelocity().setY(1.25).multiply(0.35));
//                if(jumps <= 0) return;
//                new BukkitRunnable() {
//                    @Override
//                    public void run() {
//                        Location location = player.getLocation().clone().subtract(0, 1, 0);
//                        player.sendBlockChange(location, Material.BARRIER.createBlockData());
//                        new BukkitRunnable() {
//                            @Override
//                            public void run() {
//                                player.sendBlockChange(location, location.getBlock().getBlockData());
//                            }
//                        }.runTaskLater(Lunix.this, 5);
//                    }
//                }.runTaskLater(this, 10);
//
//            }
//        }
//    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        if (event.isFlying()) {
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
            event.setCancelled(true); // Hủy sự kiện để ngăn chặn người chơi nhảy lên.
            int jumps = this.jumps.getOrDefault(event.getPlayer(), 30);
            if (jumps <= 0) {
                event.getPlayer().setAllowFlight(false);
                return;
            }
            event.getPlayer().setAllowFlight(true); // Cho phép người chơi bay.
            Vector velocity = event.getPlayer().getLocation().getDirection();
            velocity.setY(1.25); // Thay đổi giá trị này theo ý muốn của bạn.
            velocity.multiply(0.35);
            event.getPlayer().setVelocity(velocity);
            this.jumps.put(event.getPlayer(), jumps - 1);
        }
    }

    private final Map<UUID, Location> bombThrowLocations = new HashMap<>();
    private final Map<UUID, Long> rightClickTimestamp = new HashMap<>();
    private final Map<UUID, BukkitRunnable> task = new HashMap<>();
    private final Map<UUID, ParabolaEffect> particle = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (player.getInventory().getHeldItemSlot() == 0) {
            // Người chơi đang giữ phím số 1
            player.sendMessage("Bạn đang giữ phím số 1!");
        }

        if (event.getItem() != null && event.getItem().getType() == Material.BLAZE_ROD) {
            if (event.getAction().toString().contains("RIGHT")) {
                // Người chơi giữ chuột phải, đánh dấu là đang giữ
                rightClickTimestamp.put(playerUUID, System.currentTimeMillis());
                if (task.containsKey(playerUUID)) {
                    task.get(playerUUID).cancel();
                }

                if (!particle.containsKey(playerUUID)) {
                    this.particle.put(playerUUID, new ParabolaEffect(this));
                }

                BukkitRunnable runnable = new BukkitRunnable() {
                    int count = 0;

                    @Override
                    public void run() {
                        if (count >= 5) {
                            if (rightClickTimestamp.getOrDefault(playerUUID, -1L) != -1) {
                                // Lấy vị trí đang nhắm chọn
                                Location targetLocation = bombThrowLocations.get(playerUUID);
                                if (targetLocation != null) {
                                    // Thực hiện công việc của bạn, ví dụ: tạo một trái bom
                                    player.sendMessage("Quăng trái bom tại: " + targetLocation.toString());
                                    Objects.requireNonNull(targetLocation.getWorld()).createExplosion(targetLocation, 1);

                                    // Sau khi sử dụng, loại bỏ vị trí đã chọn và đặt lại trạng thái chuột phải
                                    bombThrowLocations.remove(playerUUID);
                                    rightClickTimestamp.remove(playerUUID);
                                    task.remove(playerUUID);
                                    particle.remove(playerUUID);
                                    cancel();
                                }
                            }
                            return;
                        }
                        count++;
                    }
                };
                runnable.runTaskTimer(this, 0, 1);
                task.put(playerUUID, runnable);
            }
        }
    }

    private final Map<UUID, BukkitRunnable> runnableMap = Maps.newHashMap();

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        // Kiểm tra xem slot mới được chọn có phải là slot của phím số 1 không
        if (event.getNewSlot() == 0) {
            // Người chơi đang giữ phím số 1
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove2(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // Kiểm tra xem người chơi có đang giữ chuột phải không
        if (rightClickTimestamp.containsKey(playerUUID)) {
            // Lấy vị trí đang nhắm chọn (có thể là vị trí mà đang nhắm đến trong mắt người chơi)
            Location previousLocation = bombThrowLocations.getOrDefault(playerUUID, null);
            Location targetLocation = player.getTargetBlock(null, 10).getLocation();
            Location newLocation = Objects.requireNonNull(targetLocation.getWorld())
                    .getHighestBlockAt(targetLocation).getLocation();
            ParabolaEffect parabolaEffect = particle.get(playerUUID);

            bombThrowLocations.put(playerUUID, newLocation);
            parabolaEffect.displayParabola(player.getLocation(), newLocation);

            event.getPlayer().sendBlockChange(newLocation, Material.RED_WOOL.createBlockData());
            if (previousLocation != null && !previousLocation.equals(newLocation))
                event.getPlayer().sendBlockChange(previousLocation, previousLocation.getBlock().getBlockData());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.jumps.putIfAbsent(event.getPlayer(), 30);
        MessageUtils.sendMessage(event.getPlayer(), "&aBạn được tặng 30 điểm nhảy!");
        if(worldGeneration.getWorld() != null)
            player.teleport(worldGeneration.getWorld().getSpawnLocation());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
    }


    public void spawnFilledSemicircle(Player player, double radius) {
        Location playerLoc = player.getLocation(); // Get the player's location
        Vector direction = playerLoc.getDirection().setY(0).normalize(); // Get the player's horizontal look direction

        // Calculate the normal to the direction vector for the semicircle plane
        Vector normal = new Vector(-direction.getZ(), 0, direction.getX()).normalize();

        // Determine the number of particles based on the area
//        int particles = (int) (Math.PI * radius * radius / 4); // Quarter-circle area for density
        int particles = 100;// Quarter-circle area for density

        for (int i = 0; i < particles; i++) {
            // Randomly choose a point within the semicircle
            double angle = Math.random() * Math.PI; // Angle between 0 and PI for a semicircle
            double distance = Math.sqrt(Math.random()) * radius; // Random distance within the semicircle, square root for uniform distribution

            double offsetX = distance * Math.cos(angle);
            double offsetZ = distance * Math.sin(angle);

            // Rotate the offset to align with the player's look direction
            Vector rotatedOffset = normal.clone().multiply(offsetX).add(direction.clone().multiply(offsetZ));


            // Calculate the final location of the particle
            Location particleLoc = playerLoc.clone().add(rotatedOffset);

            // Spawn the particle at this location
//            player.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 1, 0, 0, 0, 0,
//                    new Particle.DustOptions(Color.YELLOW, 1), true);
            particleLoc.getBlock().setType(Material.DIAMOND_BLOCK);
        }
    }

    @EventHandler
    public void onFoodChange(EntityRegainHealthEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        Vector push = player.getLocation().clone().getDirection().multiply(1.5);
        player.setWalkSpeed(1);
        Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> player.setWalkSpeed(0.2f), 100);
//        spawnFilledSemicircle(player, 4);
//        XParticle.circle(2, 25, ParticleDisplay.colored(
//                player.getLocation().clone().add(0, 1, 0), java.awt.Color.RED, 1f));
//        List<Location> locations = createCircle(location, 6);
//        locations.forEach(l -> {
//            double distance = l.distanceSquared(location);
//            WrapperPlayServerBlockBreakAnimation breakAnimation = new WrapperPlayServerBlockBreakAnimation();
//            breakAnimation.setPos(new BlockPosition(l.toVector()));
//            breakAnimation.setId(new Random().nextInt(5000));
//            breakAnimation.setProgress(9 - (int) Math.sqrt(distance));
//            Bukkit.getOnlinePlayers().forEach(breakAnimation::sendPacket);
//        });
//
//        CustomStack stack = CustomStack.getInstance("trove:ultimate_candybarbarian");
//        if(stack != null) {
//            Block target = player.getTargetBlock(null, 8);
//            if(target != null) {
//                getParabolicCurve(player.getLocation(), target.getLocation()).forEach(l -> {
//                    FallingBlock fallingBlock = l.getWorld().spawnFallingBlock(l, Material.DIAMOND_BLOCK.createBlockData());
//                    fallingBlock.setDropItem(false);
//                });
//                Location blockLocation = target.getLocation();
//                Item item = location.getWorld().spawn(blockLocation.clone().add(0, 8, 0), Item.class);
//                item.setUnlimitedLifetime(true);
//                item.setItemStack(stack.getItemStack());
//                item.setPickupDelay(Integer.MAX_VALUE);
//                item.setInvulnerable(true);
//                item.setVelocity(item.getVelocity().normalize().multiply(0.25));
//
//                new BukkitRunnable() {
//                    @Override
//                    public void run() {
//                        Location itemLocation = item.getLocation();
//                        if (item.isOnGround()) {
//                            item.remove();
//                            ParticleDisplay display = ParticleDisplay.colored(itemLocation.clone().add(0, 1, 0), java.awt.Color.MAGENTA, 1);
//                            new BukkitRunnable() {
//                                double radius = 0.5;
//                                int rate = 0;
//                                @Override
//                                public void run() {
//                                    if(radius > 10) {
//                                        cancel();
//                                        return;
//                                    }
//                                    XParticle.circle(radius, 25 + rate, display);
//                                    radius+=0.5;
//                                    rate+=2;
//                                }
//                            }.runTaskTimerAsynchronously(Lunix.this, 0, 2);
//                            XSound.ENTITY_FIREWORK_ROCKET_BLAST.play(itemLocation);
//                            cancel();
//                        }
//                    }
//                }.runTaskTimer(this, 0, 1);
//            }
//        }
//        new BukkitRunnable() {
//            int radius = 1;
//            double height = 0.25;
//
//            int count = 1;
//
//
//            @Override
//            public void run() {
//                if (radius >= 6) {
//                    cancel();
//                    return;
//                }
//
//                CircleGenerator.generateCircle(
//                        location, radius,
//                        CircleGenerator.Plane.XZ,
//                        true, false).forEach(l -> {
////                    fallingBlock.setHurtEntities(false);
////                    fallingBlock.setDropItem(false);
////                    fallingBlock.setVelocity(fallingBlock.getVelocity().setY(height));
////                    fallingBlock.setMetadata("test", new FixedMetadataValue(Lunix.this, true));
//
//                });
//                count++;
//                radius++;
//                height += .025;
//            }
//        }.runTaskTimer(this, 0, 2);
//
//        new BukkitRunnable() {
//            int count = 1;
//
//            @Override
//            public void run() {
//                if (count >= 6) {
//                    cancel();
//                    locations.forEach(l -> {
//                        WrapperPlayServerBlockBreakAnimation breakAnimation = new WrapperPlayServerBlockBreakAnimation();
//                        breakAnimation.setPos(new BlockPosition(l.toVector()));
//                        breakAnimation.setId(new Random().nextInt(5000));
//                        breakAnimation.setProgress(0);
//                        Bukkit.getOnlinePlayers().forEach(breakAnimation::sendPacket);
//                    });
//                    return;
//                }
//                locations.forEach(l -> {
//                    double distance = l.distanceSquared(location);
//                    WrapperPlayServerBlockBreakAnimation breakAnimation = new WrapperPlayServerBlockBreakAnimation();
//                    breakAnimation.setPos(new BlockPosition(l.toVector()));
//                    breakAnimation.setId(new Random().nextInt(5000));
//                    breakAnimation.setProgress(9 - (int) Math.sqrt(distance) - count);
//                    Bukkit.getOnlinePlayers().forEach(breakAnimation::sendPacket);
//                });
//                count++;
//            }
//        }.runTaskTimerAsynchronously(this, 100, 20);
    }

    public List<Location> getParabolicCurve(Location start, Location end) {
        List<Location> results = Lists.newArrayList();

        Vector line = end.clone().subtract(start).toVector();
        line.setY(start.getY());
        double length = line.length();
        line.normalize();

        for (double x = 0; x < length; x += 15) {
            double y = 3 * Math.pow(x, 2) + x;
            Location point = start.clone();
            point.add(line.multiply(x)).setY(point.getY() + y);
            results.add(point);
            line.multiply(1 / x);
        }
        return results;
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        if (entity.hasMetadata("test")) event.setCancelled(true);
    }

    public List<Location> getCircle(Location center, double radius, int amount) {
        List<Location> locations = new ArrayList<>();
        World world = center.getWorld();
        double increment = (2 * Math.PI) / amount;
        for (int i = 0; i < amount; i++) {
            double angle = i * increment;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            locations.add(new Location(world, x, center.getY(), z));
        }
        return locations;
    }

    public List<Location> createCircle(Location center, int radius) {
        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();
        List<Location> locations = Lists.newArrayList();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z <= radius * radius) {
                    Location loc = new Location(center.getWorld(), cx + x, cy, cz + z);
                    for (int i = 1; i <= 2; i++) {
                        Location clone = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                        Location upper = clone.clone().add(0, i, 0);
                        Location lower = clone.clone().subtract(0, i, 0);
                        if (!upper.getBlock().getType().isAir() || upper.getBlock().getType().isSolid())
                            locations.add(upper);
                        if (!lower.getBlock().getType().isAir() || upper.getBlock().getType().isSolid())
                            locations.add(lower);
                    }
                    locations.add(Objects.requireNonNull(center.getWorld()).getBlockAt(loc).getLocation());
                }
            }
        }
        return locations;
    }

    private void fakeSlot(Player player, int slot, ItemStack item) {
        PacketPlayOutSetSlot packet = new PacketPlayOutSetSlot(0, 0, slot,
                CraftItemStack.asNMSCopy(item));
        ((CraftPlayer) player).getHandle().b.a(packet);
    }

    private void fakeEquipment(Player player, List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipments) {
        WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment();
        packet.setSlots(equipments);
        packet.setEntity(player.getEntityId());
        packet.sendPacket(player);
    }


    public Vector calculateVelocity(Vector from, Vector to, int heightGain) {
        // Gravity of a potion
        double gravity = 0.115;

        // Block locations
        int endGain = to.getBlockY() - from.getBlockY();
        double horizDist = Math.sqrt(distanceSquared(from, to));

        // Height gain
        int gain = heightGain;
        double maxGain = gain > (endGain + gain) ? gain : (endGain + gain);

        // Solve quadratic equation for velocity
        double a = -horizDist * horizDist / (4 * maxGain);
        double b = horizDist;
        double c = -endGain;
        double slope = -b / (2 * a) - Math.sqrt(b * b - 4 * a * c) / (2 * a);

        // Vertical velocity
        double vy = Math.sqrt(maxGain * gravity);

        // Horizontal velocity
        double vh = vy / slope;

        // Calculate horizontal direction
        int dx = to.getBlockX() - from.getBlockX();
        int dz = to.getBlockZ() - from.getBlockZ();
        double mag = Math.sqrt(dx * dx + dz * dz);
        double dirx = dx / mag;
        double dirz = dz / mag;

        // Horizontal velocity components
        double vx = vh * dirx;
        double vz = vh * dirz;

        return new Vector(vx, vy, vz);
    }

    private double distanceSquared(Vector from, Vector to) {
        double dx = to.getBlockX() - from.getBlockX();
        double dz = to.getBlockZ() - from.getBlockZ();
        return dx * dx + dz * dz;
    }

    private void initProtocol() {
        this.protocol.addPacketListener(new PacketAdapter(this,
                ListenerPriority.NORMAL,
                PacketType.Play.Client.RECIPE_SETTINGS,
                PacketType.Play.Client.USE_ITEM) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                PacketContainer packet = event.getPacket();
                if (packet.getType() == PacketType.Play.Client.RECIPE_SETTINGS) {
                    event.setCancelled(true);
//                if(isOpen) {
//                    WrapperPlayClientRecipeSettings recipeSettings = new WrapperPlayClientRecipeSettings();
//                    recipeSettings.setBookType(WrapperPlayClientRecipeSettings.RecipeBookType.CRAFTING);
//                    recipeSettings.setIsOpen(false);
//                    recipeSettings.sendPacket(player);
//                }
                }
            }
        });
    }

    private void initRunnable() {
        this.runnable = new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {

                });
            }
        };
        this.runnable.runTaskTimerAsynchronously(this, 0, 1);
    }
}
