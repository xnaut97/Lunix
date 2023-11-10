package com.github.tezvn.lunix;

import com.github.tezvn.lunix.chat.v1.MessageUtils;
import com.github.tezvn.lunix.menu.Menu;
import com.github.tezvn.lunix.transaction.PlayerTransaction;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;


@Getter
public class Lunix extends JavaPlugin implements Listener {

    private boolean registered;

    private Map<Player, Integer> jumps = Maps.newHashMap();

    @Override
    public void onEnable() {
        Menu.register(this);
        Bukkit.getPluginManager().registerEvents(this, this);
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
                if(task.containsKey(playerUUID)) {
                    task.get(playerUUID).cancel();
                }

                if(!particle.containsKey(playerUUID)) {
                    this.particle.put(playerUUID, new ParabolaEffect(this));
                }

                BukkitRunnable runnable = new BukkitRunnable() {
                    int count = 0;
                    @Override
                    public void run() {
                        if(count >= 5) {
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
        this.jumps.putIfAbsent(event.getPlayer(), 30);
        MessageUtils.sendMessage(event.getPlayer(), "&aBạn được tặng 30 điểm nhảy!");
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

}
