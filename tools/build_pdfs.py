#!/usr/bin/env python3
"""Convierte los manuales Markdown a PDF reales usando reportlab.
No depende de Internet. Soporta encabezados, listas, tablas simples,
bloques de código y caracteres españoles (á é í ó ú ñ ¿ ¡).
"""
import os
import re
import html

from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import cm
from reportlab.lib import colors
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, PageBreak, ListFlowable, ListItem,
    Table, TableStyle, Preformatted
)
from reportlab.lib.enums import TA_LEFT, TA_CENTER

BASE = os.path.join(os.path.dirname(__file__), "..")
DOCS = os.path.join(BASE, "docs")
OUT = os.path.join(DOCS, "pdf")
os.makedirs(OUT, exist_ok=True)

styles = getSampleStyleSheet()
styles.add(ParagraphStyle(name="H1Custom", fontSize=20, leading=24, spaceAfter=14, spaceBefore=6, textColor=colors.HexColor("#14335A"), fontName="Helvetica-Bold"))
styles.add(ParagraphStyle(name="H2Custom", fontSize=15, leading=19, spaceAfter=10, spaceBefore=16, textColor=colors.HexColor("#14335A"), fontName="Helvetica-Bold"))
styles.add(ParagraphStyle(name="H3Custom", fontSize=12.5, leading=16, spaceAfter=8, spaceBefore=12, textColor=colors.HexColor("#2C5B94"), fontName="Helvetica-Bold"))
styles.add(ParagraphStyle(name="BodyCustom", fontSize=10, leading=14.5, spaceAfter=8, alignment=TA_LEFT, fontName="Helvetica"))
styles.add(ParagraphStyle(name="CodeCustom", fontSize=8.3, leading=11, fontName="Courier", backColor=colors.HexColor("#F2F4F6"), borderPadding=6, spaceAfter=10))
styles.add(ParagraphStyle(name="CoverTitle", fontSize=28, leading=34, alignment=TA_CENTER, textColor=colors.HexColor("#14335A"), fontName="Helvetica-Bold"))
styles.add(ParagraphStyle(name="CoverSub", fontSize=13, leading=18, alignment=TA_CENTER, textColor=colors.HexColor("#57626D"), spaceBefore=10))


def esc(text):
    text = html.escape(text, quote=False)
    text = re.sub(r"\*\*(.+?)\*\*", r"<b>\1</b>", text)
    text = re.sub(r"`([^`]+)`", r'<font face="Courier">\1</font>', text)
    text = re.sub(r"\[([^\]]+)\]\(([^)]+)\)", r"\1", text)  # enlaces -> sólo texto
    return text


def parse_table(lines, idx):
    rows = []
    while idx < len(lines) and lines[idx].strip().startswith("|"):
        row = [c.strip() for c in lines[idx].strip().strip("|").split("|")]
        rows.append(row)
        idx += 1
    if len(rows) >= 2 and set(rows[1][0]) <= set("-: "):
        rows.pop(1)
    return rows, idx


def markdown_to_flowables(md_text, title, subtitle):
    story = []
    story.append(Spacer(1, 6 * cm))
    story.append(Paragraph(esc(title), styles["CoverTitle"]))
    story.append(Paragraph(esc(subtitle), styles["CoverSub"]))
    story.append(Paragraph("A Tu Manera — v1.0.0", styles["CoverSub"]))
    story.append(PageBreak())

    lines = md_text.split("\n")
    i = 0
    in_code = False
    code_buffer = []
    list_buffer = []

    def flush_list():
        nonlocal list_buffer
        if list_buffer:
            items = [ListItem(Paragraph(esc(t), styles["BodyCustom"]), leftIndent=12) for t in list_buffer]
            story.append(ListFlowable(items, bulletType="bullet", start="•"))
            list_buffer = []

    while i < len(lines):
        line = lines[i]
        stripped = line.strip()

        if stripped.startswith("```"):
            if not in_code:
                in_code = True
                code_buffer = []
            else:
                in_code = False
                flush_list()
                code_text = "\n".join(code_buffer)
                story.append(Preformatted(code_text[:3000], styles["CodeCustom"]))
            i += 1
            continue

        if in_code:
            code_buffer.append(line)
            i += 1
            continue

        if not stripped:
            flush_list()
            i += 1
            continue

        if stripped.startswith("| "):
            flush_list()
            rows, i = parse_table(lines, i)
            if rows:
                tbl = Table(rows, hAlign="LEFT", repeatRows=1)
                tbl.setStyle(TableStyle([
                    ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#14335A")),
                    ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
                    ("FONTNAME", (0, 0), (-1, 0), "Helvetica-Bold"),
                    ("FONTSIZE", (0, 0), (-1, -1), 8.3),
                    ("GRID", (0, 0), (-1, -1), 0.5, colors.HexColor("#C9D2D8")),
                    ("VALIGN", (0, 0), (-1, -1), "TOP"),
                    ("ROWBACKGROUNDS", (0, 1), (-1, -1), [colors.white, colors.HexColor("#F7F9FA")]),
                    ("LEFTPADDING", (0, 0), (-1, -1), 5),
                    ("RIGHTPADDING", (0, 0), (-1, -1), 5),
                    ("TOPPADDING", (0, 0), (-1, -1), 4),
                    ("BOTTOMPADDING", (0, 0), (-1, -1), 4),
                ]))
                story.append(tbl)
                story.append(Spacer(1, 10))
            continue

        if stripped.startswith("# "):
            flush_list()
            story.append(Paragraph(esc(stripped[2:]), styles["H1Custom"]))
        elif stripped.startswith("## "):
            flush_list()
            story.append(Paragraph(esc(stripped[3:]), styles["H2Custom"]))
        elif stripped.startswith("### "):
            flush_list()
            story.append(Paragraph(esc(stripped[4:]), styles["H3Custom"]))
        elif stripped.startswith("> "):
            flush_list()
            story.append(Paragraph(esc(stripped[2:]), ParagraphStyle(
                "Quote", parent=styles["BodyCustom"], leftIndent=14,
                textColor=colors.HexColor("#57626D"), borderColor=colors.HexColor("#F6A623"),
                borderWidth=0, backColor=colors.HexColor("#FBF6EC")
            )))
        elif stripped.startswith("- ") or stripped.startswith("* "):
            list_buffer.append(stripped[2:])
        elif re.match(r"^\d+\.\s", stripped):
            list_buffer.append(re.sub(r"^\d+\.\s", "", stripped))
        else:
            flush_list()
            story.append(Paragraph(esc(stripped), styles["BodyCustom"]))

        i += 1

    flush_list()
    return story


def build_pdf(md_filename, title, subtitle, out_filename):
    md_path = os.path.join(DOCS, md_filename)
    with open(md_path, encoding="utf-8") as f:
        md_text = f.read()

    out_path = os.path.join(OUT, out_filename)
    doc = SimpleDocTemplate(
        out_path, pagesize=A4,
        leftMargin=2.2 * cm, rightMargin=2.2 * cm, topMargin=2 * cm, bottomMargin=2 * cm,
        title=title, author="A Tu Manera"
    )
    story = markdown_to_flowables(md_text, title, subtitle)
    doc.build(story)
    print(f"Generado: {out_path}")


if __name__ == "__main__":
    build_pdf("MEMORIA_DESCRIPTIVA.md", "Memoria Descriptiva", "A Tu Manera — Ingeniería Civil / Planificación Urbana", "MEMORIA_DESCRIPTIVA.pdf")
    build_pdf("MANUAL_USUARIO.md", "Manual de Usuario", "Guía paso a paso para jugadores", "MANUAL_USUARIO.pdf")
    build_pdf("MANUAL_TECNICO.md", "Manual Técnico", "Arquitectura, stack y mantenimiento", "MANUAL_TECNICO.pdf")
