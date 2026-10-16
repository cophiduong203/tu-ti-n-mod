# Hytur Tu Tiên — 10 Cảnh Giới × 10 Tầng

**Mod bổ sung hệ tu tiên cho HyturCraft / Fabric Minecraft 1.20.1.**

> Đây là **mã nguồn dự án**. Bạn cần build thành `.jar` bằng Gradle / GitHub Actions trước khi cài. Dự án được viết với Fabric API, không phụ thuộc vào source code của HyturCraft. Chưa được xác nhận tương thích hoàn toàn với mọi phiên bản modpack.

## Hệ thống cảnh giới

| Thứ tự | Cảnh giới | Tầng toàn cục | Màu hào quang |
|---|---|---|---|
| 1 | Luyện Khí | 1–10 | Xanh lam nhạt |
| 2 | Trúc Cơ | 11–20 | Xanh ngọc |
| 3 | Kim Đan | 21–30 | Vàng kim |
| 4 | Nguyên Anh | 31–40 | Tím |
| 5 | Hóa Thần | 41–50 | Đỏ hồng |
| 6 | Luyện Hư | 51–60 | Xanh sapphire |
| 7 | Hợp Thể | 61–70 | Cam |
| 8 | Đại Thừa | 71–80 | Trắng |
| 9 | Độ Kiếp | 81–90 | Tím sấm sét |
| 10 | Chân Tiên | 91–100 | Vàng tiên quang |

- **Tu vi:** Giết quái thuộc nhóm Monster để nhận XP tu tiên độc lập với XP phù phép của Minecraft. Lượng nhận tăng theo HP tối đa của mob; quái khỏe nhận nhiều XP hơn.
- **Ngồi thiền:** Đứng trên mặt đất, **giữ Shift và đứng yên** để nhận **3 tu vi mỗi giây** mặc định. Không yêu cầu trang bị.
- **Đột phá:** Tự động tăng tầng khi đủ tu vi. Mỗi lần tăng tầng phát vòng sáng, hạt phép, âm thanh, tiêu đề lớn; lên cảnh giới mới có lễ đột phá lớn hơn và thông báo toàn server.
- **Hào quang vĩnh viễn:** Hiện quanh thân khi chơi, với màu + loại hạt riêng cho từng cảnh giới. Đây là hiệu ứng hạt (không phải thuốc), không có giới hạn thời gian, không cần bật lại mỗi lần đăng nhập. Có thể giảm mật độ trong file config.
- **Tăng sức mạnh:** Từ tầng 2 trở lên nhận cộng dồn **+0.2 HP / tầng**, **+0.055 sát thương cận chiến / tầng**, **+0.03 giáp / tầng**, **+0.00012 tốc độ di chuyển / tầng** (đều chỉnh được).
- **Lưu khi chết:** Tu vi và cảnh giới được lưu vào NBT của nhân vật, giữ nguyên sau chết/đăng xuất/đổi dimension.
- **Giảm xung đột:** Sử dụng UUID riêng cho bốn modifier thuộc tính và hiệu ứng particle vanilla, không thay thế hệ cấp độ/enchantment/trang bị của HyturCraft.

## Yêu cầu

- Minecraft Java **1.20.1**
- **Fabric Loader** cho 1.20.1 (bản từ 0.14.22)
- **Fabric API** cho 1.20.1 (khuyên dùng `0.92.2+1.20.1`)
- Java **17** để build; Java 17 hoặc cao hơn phù hợp để chạy theo yêu cầu modpack.

## Build thành .jar (cách dễ nhất: GitHub Actions)

1. Tạo một **repository GitHub** mới.
2. Giải nén ZIP dự án, upload **toàn bộ tệp bên trong** lên thư mục gốc repository (gồm cả `.github/workflows/build.yml`).
3. Vào **Actions → Build Fabric mod → Run workflow** (hoặc đẩy code lên nhánh chính để tự chạy).
4. Khi job thành công, kéo xuống **Artifacts**, tải `hytur-tu-tien-fabric-1.20.1`.
5. Giải nén artifact để lấy **`hytur-tu-tien-1.0.0.jar`**.

Build trên máy đã cài Java 17 và Gradle 8.8:

```bash
gradle --no-daemon clean build
```

File mod nằm tại `build/libs/hytur-tu-tien-1.0.0.jar`. Không cài `-sources.jar`.

## Cài đặt

- **Chơi đơn:** Chép file jar build được vào thư mục `mods/` của instance HyturCraft.
- **Chơi server:** Chủ server phải chép jar vào `mods/` **phía máy chủ** và khởi động lại. Đây là mod chạy cơ chế trên server; người chơi không cần thêm menu/hotkey phía client.
- Trước khi cài nên **sao lưu thế giới** và thử trên một bản sao modpack; các mod chỉnh thuộc tính/máu có thể ảnh hưởng sức mạnh tổng thể.

## Lệnh

| Lệnh | Công dụng | Quyền |
|---|---|---|
| `/canhgioi` hoặc `/canhgioi xem` | Xem cảnh giới và tu vi | Mọi người |
| `/canhgioi huongdan` | Hướng dẫn trong game | Mọi người |
| `/canhgioi addxp 1000` | Thêm 1.000 tu vi để thử | OP cấp 2 |
| `/canhgioi settang 21` | Đặt **Kim Đan tầng 1** để thử hào quang | OP cấp 2 |
| `/canhgioi settang 100` | Đặt **Chân Tiên tầng 10** | OP cấp 2 |
| `/canhgioi reload` | Đọc lại file cấu hình | OP cấp 2 |

## Cấu hình

Sau lần chạy đầu tiên, `config/hyturtutien.json` tự được tạo:

```json
{
  "xpRequiredMultiplier": 1.0,
  "mobXpMultiplier": 1.0,
  "meditationEnabled": true,
  "meditationXpPerSecond": 3,
  "aurasEnabled": true,
  "auraPulseTicks": 8,
  "auraParticlesPerPulse": 4,
  "broadcastNewRealm": true,
  "bonusMaxHpPerLayer": 0.2,
  "bonusAttackDamagePerLayer": 0.055,
  "bonusArmorPerLayer": 0.03,
  "bonusMovementSpeedPerLayer": 0.00012
}
```

- `xpRequiredMultiplier`: cao hơn thì cày lâu hơn. Công thức XP cơ sở để lên tầng kế tiếp: `70 + stageIndex*22 + stageIndex²*3` (`stageIndex = tầng toàn cục - 1`).
- `mobXpMultiplier`: tỷ lệ XP khi giết quái.
- `meditationXpPerSecond`: lượng tu vi mỗi giây thiền.
- `auraPulseTicks`, `auraParticlesPerPulse`: chỉnh mật độ và hiệu suất hào quang.
- `bonus...PerLayer`: mức cộng thuộc tính ở mỗi tầng. Nếu tắt hoặc đặt 0 thì không tăng sức mạnh.

Sau khi chỉnh cấu hình, dùng `/canhgioi reload` hoặc khởi động lại server.

## Giới hạn bản 1.0

- Chưa có GUI riêng, chỉ có thanh actionbar mỗi 5 giây và lệnh `/canhgioi`.
- Chưa có thiên kiếp, đan dược hay kỹ năng bay; đây là hệ nền tảng 100 tầng + hào quang.
- Chỉ những mục tiêu được phân nhóm `MONSTER` nhận thưởng tu vi khi bị người chơi trực tiếp hạ. Một số boss từ mod đặt nhóm khác có thể chưa nhận tu vi.
- Nếu một server có mod khác thay đổi hệ thống thuộc tính, cần test khả năng cân bằng sau khi cài.
- Mã nguồn chưa được biên dịch/kiểm thử trong môi trường Minecraft thực tế ở bản phát hành nguồn này.
