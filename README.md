# hSayac / hTimer

Paper sunucuları için sabit tarihli ve haftalık tekrarlayan geri sayımlar oluşturan Minecraft eklentisi. Sayaçlar YAML üzerinden tanımlanır; PlaceholderAPI ile skor tahtası, hologram, tablist ve benzeri arayüzlerde gösterilebilir.

## Özellikler

- Sabit tarih ve saatte bir kez çalışan sayaçlar (`FIXED`)
- Belirli gün ve saatte her hafta tekrarlanan sayaçlar (`WEEKLY`)
- Sayaç tamamlandığında konsol komutları çalıştırma
- `LONG` ve `SHORT` kalan süre biçimleri
- PlaceholderAPI desteği
- Yapılandırmayı sunucuyu yeniden başlatmadan yenileme
- `/htimer` komut takma adı

## Gereksinimler

- Java 17 veya üzeri
- Paper 1.20.4 (veya uyumlu Paper sürümü)
- PlaceholderAPI 2.11.7 veya üzeri

PlaceholderAPI `plugin.yml` içinde zorunlu bağımlılık olarak tanımlıdır. Bu nedenle PlaceholderAPI sunucuda yoksa hSayac yüklenmez.

## Kurulum

1. `target/hSayac-1.0.jar` dosyasını sunucunun `plugins/` klasörüne kopyalayın.
2. PlaceholderAPI eklentisinin kurulu olduğundan emin olun.
3. Sunucuyu başlatın veya yeniden başlatın.
4. İlk çalıştırmadan sonra oluşan `plugins/hSayac/config.yml` dosyasını düzenleyin.
5. Değişiklikleri `/hsayac reload` ile uygulayın.

## Komutlar ve izinler

| Komut | Açıklama | İzin |
|---|---|---|
| `/hsayac reload` | Yapılandırmayı yeniden yükler ve sayaçları yeniden hesaplar | `hsayac.admin` |
| `/htimer reload` | `/hsayac` için alternatif komut adı | `hsayac.admin` |

Yetkisi olmayan kullanıcılar komut çalıştırdığında `Yetkin yok.` mesajını alır.

## Sayaç yapılandırması

Sayaçlar `config.yml` içindeki `counters` altında tanımlanır. Her sayaç için benzersiz bir anahtar kullanın:

```yaml
counters:
  sezon_bitis:
    type: "FIXED"
    date: "2026-12-31 23:59"
    format: "LONG"
    end-message: "&cSayaç tamamlandı."
    commands:
      - "broadcast &aSayaç tamamlandı!"

  haftalik_event:
    type: "WEEKLY"
    day: "SUNDAY"
    time: "20:00"
    format: "SHORT"
    end-message: "&bHaftalık etkinlik başladı!"
    commands:
      - "broadcast &aEtkinlik başladı!"
```

### Alanlar

| Alan | Tür | Açıklama |
|---|---|---|
| `type` | `FIXED` / `WEEKLY` | Sayaç türü. Varsayılan: `FIXED` |
| `date` | `YYYY-MM-dd HH:mm` | `FIXED` sayaçların bitiş zamanı |
| `day` | İngilizce gün adı | `WEEKLY` sayaçlarda: `MONDAY`–`SUNDAY` |
| `time` | `HH:mm` | `WEEKLY` sayaçların saati, 24 saat formatı |
| `format` | `LONG` / `SHORT` | Kalan sürenin gösterim biçimi |
| `end-message` | Metin | Sayaç bittikten sonra placeholder tarafından döndürülen metin |
| `commands` | Komut listesi | Sayaç tamamlandığında konsoldan çalıştırılır; başına `/` koymayın |

`FIXED` sayaç bir kez çalışır ve tamamlandı olarak işaretlenir. `WEEKLY` sayaç bir sonraki haftaya otomatik hesaplanır. Zaman hesabı sunucunun sistem saatine göre yapılır.

## PlaceholderAPI

Eklenti iki identifier kaydeder: `hsayac` ve `htimer`.

```text
%hsayac_kalan_<sayaç_adı>%
%htimer_kalan_<sayaç_adı>%
```

Örnek:

```text
%hsayac_kalan_sezon_bitis%
%htimer_kalan_haftalik_event%
```

Sayaç adı bulunamazsa boş değer döndürülür. `LONG` biçimi örneğin `3 gün 4 saat 12 dakika 8 saniye`, `SHORT` biçimi ise `3g 4s 12d 8s` olarak gösterilir.

## Derleme

Proje Maven kullanır. Java 17 ve Maven kurulu bir geliştirme ortamında:

```bash
mvn clean package
```

Başarılı derleme sonunda kullanılacak dosya:

```text
target/hSayac-1.0.jar
```

Bu proje için temiz derleme 25 Ekim 2026 tarihinde başarıyla doğrulanmıştır. Derleme sırasında yalnızca Maven Shade Plugin'in manifest dosyası örtüşmesi ve Java 17 için `--release` önerisiyle ilgili uyarılar görülmüştür; derlemeyi engelleyen bir hata yoktur. Test kaynakları bulunmadığı için otomatik test çalıştırılmamıştır.

## Proje yapısı

```text
src/main/java/github/heyetcoolio/hSayac/
├── HSayac.java             # Eklenti yaşam döngüsü, sayaç yükleme ve kontrol görevi
├── SeasonCommand.java      # /hsayac ve reload komutu
└── SeasonPlaceholder.java  # PlaceholderAPI entegrasyonu

src/main/resources/
├── plugin.yml               # Eklenti metadatası, komut ve bağımlılıklar
└── config.yml               # Örnek sayaçlar ve mesajlar
```

## Çalışma modeli

Eklenti sayaçları açılışta `config.yml` dosyasından yükler ve her saniye hedef zamana ulaşılıp ulaşılmadığını kontrol eder. Komutlar ana sunucu iş parçacığında çalıştırılır. Sayaçların kalan süresi `LocalDateTime` ve sunucunun sistem saatine göre hesaplanır.

## Bilinen davranışlar ve dikkat edilmesi gerekenler

- `end-message` oyunculara ayrıca mesaj olarak gönderilmez; sayaç tamamlandıktan sonra placeholder sonucu olarak kullanılır.
- `commands` listesindeki komutlar konsoldan çalışır ve komutların başında `/` bulunmamalıdır.
- `date` değeri geçmişteyse sabit sayaç ilk kontrol döngüsünde tamamlanır.
- Üretim sunucusunda `broadcast`, `whitelist on`, ekonomi veya bakım komutlarını kullanmadan önce komutların kurulu eklentiler tarafından desteklendiğini doğrulayın.
- Kodda henüz otomatik birim/integrasyon testi bulunmamaktadır; gerçek çalışma doğrulaması Paper sunucusu üzerinde yapılmalıdır.

## Lisans ve destek

Copyright (c) 2025 heyetcoolio. Lisans ayrıntıları için [LICENSE](LICENSE) dosyasına bakın.

Destek: [DedeSoftware Discord](https://discord.gg/4d3KhsJhwF)

Proje: [github.com/heyetcoolio/hTimer](https://github.com/heyetcoolio/hTimer)
