#!/usr/bin/env python3
"""Genera los vector drawables (XML) usados como identidad visual de la app.
No depende de Internet ni de librerías externas: sólo compone formas
geométricas simples (círculos, rectángulos, polígonos) en pathData de
Android VectorDrawable. Se ejecuta una sola vez para poblar res/drawable.
"""
import math
import os

OUT_DIR = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "res", "drawable")
os.makedirs(OUT_DIR, exist_ok=True)

VIEWPORT = 108  # estándar para iconos "adaptativos" grandes; se reescala igual para iconos pequeños


def circle_path(cx, cy, r):
    return f"M{cx - r},{cy} a{r},{r} 0 1,0 {2*r},0 a{r},{r} 0 1,0 {-2*r},0 Z"


def rect_path(x, y, w, h, rx=0):
    if rx <= 0:
        return f"M{x},{y} L{x+w},{y} L{x+w},{y+h} L{x},{y+h} Z"
    # rounded rect approximated with straight corners cut (simple octagon-ish) for cleanliness
    return (
        f"M{x+rx},{y} L{x+w-rx},{y} Q{x+w},{y} {x+w},{y+rx} L{x+w},{y+h-rx} "
        f"Q{x+w},{y+h} {x+w-rx},{y+h} L{x+rx},{y+h} Q{x},{y+h} {x},{y+h-rx} "
        f"L{x},{y+rx} Q{x},{y} {x+rx},{y} Z"
    )


def polygon_path(points):
    pts = " L".join(f"{x},{y}" for x, y in points)
    return f"M{pts} Z"


def triangle_path(cx, cy, size):
    return polygon_path([(cx, cy - size), (cx - size, cy + size), (cx + size, cy + size)])


def teardrop_path(cx, cy, r):
    # gota de agua: círculo inferior + punta superior triangular
    return polygon_path([(cx, cy - r * 1.6), (cx - r, cy + r * 0.4), (cx, cy + r * 1.1), (cx + r, cy + r * 0.4)])


def svg(paths, size=VIEWPORT):
    body = "\n".join(
        f'    <path android:fillColor="{color}" android:pathData="{d}"/>' for d, color in paths
    )
    return f'''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="{size}dp"
    android:height="{size}dp"
    android:viewportWidth="{size}"
    android:viewportHeight="{size}">
{body}
</vector>
'''


def write(name, paths, size=VIEWPORT):
    with open(os.path.join(OUT_DIR, f"{name}.xml"), "w", encoding="utf-8") as f:
        f.write(svg(paths, size))


def badge_wrap(inner_paths, ring_color, bg_color="#FFFFFF"):
    v = VIEWPORT
    c = v / 2
    r_outer = v * 0.48
    r_inner = v * 0.40
    paths = [
        (circle_path(c, c, r_outer), ring_color),
        (circle_path(c, c, r_inner), bg_color),
    ]
    paths.extend(inner_paths)
    return paths


# ---------------------------------------------------------------------------
# Iconos de infraestructura (13) - usados en catálogo de construcción y tiles
# ---------------------------------------------------------------------------
v = VIEWPORT
c = v / 2

write("ic_infra_road", [
    (rect_path(10, 34, 88, 40, rx=6), "#3A4650"),
    (rect_path(24, 50, 14, 8), "#F4D35E"),
    (rect_path(50, 50, 14, 8), "#F4D35E"),
    (rect_path(76, 50, 14, 8), "#F4D35E"),
])

write("ic_infra_house_small", [
    (polygon_path([(c, 18), (86, 50), (74, 50), (74, 92), (34, 92), (34, 50), (22, 50)]), "#EF6F6C"),
    (rect_path(47, 66, 16, 26), "#7C4A3D"),
])

write("ic_infra_house_block", [
    (rect_path(20, 24, 68, 68, rx=4), "#EF6F6C"),
    (rect_path(30, 36, 12, 12), "#FFF3EC"),
    (rect_path(48, 36, 12, 12), "#FFF3EC"),
    (rect_path(66, 36, 12, 12), "#FFF3EC"),
    (rect_path(30, 54, 12, 12), "#FFF3EC"),
    (rect_path(48, 54, 12, 12), "#FFF3EC"),
    (rect_path(66, 54, 12, 12), "#FFF3EC"),
])

write("ic_infra_school", [
    (polygon_path([(c, 16), (92, 46), (16, 46)]), "#8B6FE0"),
    (rect_path(26, 46, 56, 44), "#A48CEA"),
    (rect_path(48, 66, 12, 24), "#4B2E83"),
])

write("ic_infra_library", [
    (rect_path(20, 24, 16, 64), "#8B6FE0"),
    (rect_path(38, 30, 16, 58), "#A48CEA"),
    (rect_path(56, 24, 16, 64), "#8B6FE0"),
    (rect_path(74, 34, 14, 54), "#A48CEA"),
])

write("ic_infra_health_center", [
    (rect_path(16, 16, 76, 76, rx=10), "#E0554F"),
    (rect_path(46, 30, 16, 48), "#FFFFFF"),
    (rect_path(30, 46, 48, 16), "#FFFFFF"),
])

write("ic_infra_hospital", [
    (rect_path(12, 12, 84, 84, rx=12), "#C7362F"),
    (rect_path(44, 26, 20, 56), "#FFFFFF"),
    (rect_path(26, 44, 56, 20), "#FFFFFF"),
])

write("ic_infra_park_small", [
    (circle_path(c, 40, 26), "#57B15F"),
    (rect_path(48, 60, 12, 30), "#7C4A3D"),
])

write("ic_infra_park_large", [
    (circle_path(34, 38, 20), "#57B15F"),
    (circle_path(66, 30, 24), "#4CA057"),
    (circle_path(80, 46, 16), "#57B15F"),
    (rect_path(48, 56, 12, 34), "#7C4A3D"),
])

write("ic_infra_water_tower", [
    (teardrop_path(c, 30, 22), "#3AA6D6"),
    (rect_path(30, 66, 8, 26), "#7C8894"),
    (rect_path(70, 66, 8, 26), "#7C8894"),
    (rect_path(24, 60, 60, 10), "#5B7482"),
])

write("ic_infra_water_plant", [
    (rect_path(16, 44, 76, 44, rx=6), "#3AA6D6"),
    (teardrop_path(c, 26, 18), "#8FD3EE"),
    (rect_path(30, 54, 14, 24), "#1F7FA8"),
    (rect_path(64, 54, 14, 24), "#1F7FA8"),
])

write("ic_infra_bus", [
    (rect_path(14, 30, 80, 44, rx=10), "#E08A3C"),
    (rect_path(22, 40, 18, 16), "#FFF3E4"),
    (rect_path(44, 40, 18, 16), "#FFF3E4"),
    (circle_path(30, 80, 9), "#3A4650"),
    (circle_path(78, 80, 9), "#3A4650"),
])

write("ic_infra_train", [
    (rect_path(24, 18, 60, 58, rx=12), "#E08A3C"),
    (rect_path(32, 30, 18, 18), "#FFF3E4"),
    (rect_path(58, 30, 18, 18), "#FFF3E4"),
    (circle_path(36, 84, 8), "#3A4650"),
    (circle_path(72, 84, 8), "#3A4650"),
])

# ---------------------------------------------------------------------------
# Iconos de módulo (7) - usados como cabecera de cada pantalla de construcción
# ---------------------------------------------------------------------------
write("ic_module_road", [(circle_path(c, c, v*0.46), "#3A4650"), (rect_path(30, c-6, 48, 12), "#F4D35E")])
write("ic_module_housing", [(circle_path(c, c, v*0.46), "#EF6F6C"), (polygon_path([(c, 26), (80, 56), (28, 56)]), "#FFF3EC")])
write("ic_module_education", [(circle_path(c, c, v*0.46), "#8B6FE0"), (rect_path(34, 34, 40, 40, rx=6), "#FFFFFF")])
write("ic_module_health", [(circle_path(c, c, v*0.46), "#E0554F"), (rect_path(46, 30, 16, 48), "#FFFFFF"), (rect_path(30, 46, 48, 16), "#FFFFFF")])
write("ic_module_park", [(circle_path(c, c, v*0.46), "#57B15F"), (circle_path(c, 46, 20), "#DFF3E1")])
write("ic_module_water", [(circle_path(c, c, v*0.46), "#3AA6D6"), (teardrop_path(c, c, 20), "#EAF8FD")])
write("ic_module_transport", [(circle_path(c, c, v*0.46), "#E08A3C"), (rect_path(34, 40, 40, 26, rx=8), "#FFF3E4")])

# ---------------------------------------------------------------------------
# Insignias (12) - recompensas ilustradas
# ---------------------------------------------------------------------------
write("ic_badge_road", badge_wrap([(rect_path(30, c-6, 48, 12), "#3A4650"), (rect_path(46, c-3, 8, 6), "#F4D35E")], "#3A4650"))
write("ic_badge_house", badge_wrap([(polygon_path([(c, 30), (72, 54), (36, 54)]), "#EF6F6C"), (rect_path(42, 54, 24, 20), "#EF6F6C")], "#EF6F6C"))
write("ic_badge_school", badge_wrap([(polygon_path([(c, 28), (76, 50), (32, 50)]), "#8B6FE0"), (rect_path(40, 50, 28, 22), "#8B6FE0")], "#8B6FE0"))
write("ic_badge_mobility", badge_wrap([(circle_path(c, c, 20), "#F6A623"), (rect_path(c-3, c-14, 6, 14), "#FFFFFF")], "#F6A623"))
write("ic_badge_green", badge_wrap([(circle_path(c-10, 50, 14), "#4CAF6D"), (circle_path(c+10, 44, 16), "#2E8B52")], "#4CAF6D"))
write("ic_badge_health", badge_wrap([(rect_path(c-5, 36, 10, 32), "#E0554F"), (rect_path(38, c-5, 32, 10), "#E0554F")], "#E0554F"))
write("ic_badge_water", badge_wrap([(teardrop_path(c, c, 16), "#3AA6D6")], "#3AA6D6"))
write("ic_badge_education", badge_wrap([(rect_path(36, 40, 36, 26, rx=4), "#8B6FE0"), (rect_path(44, 66, 20, 6), "#8B6FE0")], "#8B6FE0"))
write("ic_badge_budget", badge_wrap([(circle_path(c, c, 18), "#F6A623"), (rect_path(c-3, c-10, 6, 20), "#FFFFFF")], "#F6A623"))
write("ic_badge_missions10", badge_wrap([(polygon_path([(c, 34), (c+10, 50), (c+28, 52), (c+14, 64), (c+18, 82), (c, 72), (c-18, 82), (c-14, 64), (c-28, 52), (c-10, 50)]), "#35A369")], "#35A369"))
write("ic_badge_missions20", badge_wrap([(polygon_path([(c, 32), (c+11, 50), (c+30, 53), (c+16, 66), (c+19, 85), (c, 74), (c-19, 85), (c-16, 66), (c-30, 53), (c-11, 50)]), "#2E8B52")], "#2E8B52"))
write("ic_badge_xp", badge_wrap([(polygon_path([(c, 28), (c+14, c), (c, c+28), (c-14, c)]), "#F6A623")], "#F6A623"))

# ---------------------------------------------------------------------------
# Decoraciones / coleccionables (8)
# ---------------------------------------------------------------------------
write("ic_deco_fountain", badge_wrap([(circle_path(c, 62, 24), "#3AA6D6"), (teardrop_path(c, 34, 12), "#8FD3EE")], "#3AA6D6", "#EAF8FD"))
write("ic_deco_clock", badge_wrap([(circle_path(c, c, 22), "#F6A623"), (rect_path(c-3, c-16, 6, 16), "#FFFFFF"), (rect_path(c-1, c-1, 12, 5), "#FFFFFF")], "#F6A623"))
write("ic_deco_statue", badge_wrap([(rect_path(c-6, 30, 12, 34), "#B8C2CC"), (rect_path(c-16, 64, 32, 10), "#7C8894")], "#7C8894"))
write("ic_deco_garden", badge_wrap([(circle_path(c-14, 54, 14), "#4CAF6D"), (circle_path(c+2, 44, 16), "#2E8B52"), (circle_path(c+18, 56, 12), "#57B15F")], "#4CAF6D"))
write("ic_deco_bridge", badge_wrap([(rect_path(28, 58, 52, 8), "#B8862B"), (rect_path(30, 66, 6, 16), "#7C4A3D"), (rect_path(72, 66, 6, 16), "#7C4A3D")], "#B8862B"))
write("ic_deco_obelisk", badge_wrap([(polygon_path([(c, 24), (c+8, 74), (c-8, 74)]), "#B8C2CC"), (rect_path(c-14, 74, 28, 8), "#7C8894")], "#B8C2CC"))
write("ic_deco_bandstand", badge_wrap([(polygon_path([(c, 26), (c+22, 46), (c-22, 46)]), "#E08A3C"), (rect_path(c-16, 46, 32, 26), "#FCE0BE")], "#E08A3C"))
write("ic_deco_lighthouse", badge_wrap([(polygon_path([(c-10, 82), (c-6, 30), (c+6, 30), (c+10, 82)]), "#E0554F"), (rect_path(c-12, 24, 24, 10), "#F6A623")], "#E0554F"))

# ---------------------------------------------------------------------------
# Avatares (8) - alias/rostro simplificado, sin datos personales reales
# ---------------------------------------------------------------------------
avatar_palettes = [
    ("#F6A623", "#7C4A3D"), ("#3AA6D6", "#2E4A5C"), ("#EF6F6C", "#5C2E2E"), ("#57B15F", "#2E5C36"),
    ("#8B6FE0", "#3E2E5C"), ("#E08A3C", "#5C3B1E"), ("#4CC3C0", "#1E5C5A"), ("#D65DA8", "#5C1E44"),
]
for i, (skin, hair) in enumerate(avatar_palettes, start=1):
    paths = [
        (circle_path(c, c, v*0.46), "#FFFFFF"),
        (circle_path(c, c+6, 28), skin),
        (circle_path(c, c-18, 20), hair),
        (circle_path(c-10, c+2, 4), "#2A2A2A"),
        (circle_path(c+10, c+2, 4), "#2A2A2A"),
    ]
    write(f"avatar_{i}", paths)

print("Iconos generados en", OUT_DIR)
