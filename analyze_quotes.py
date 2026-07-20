# -*- coding: utf-8 -*-
import zipfile
from pathlib import Path
from xml.etree import ElementTree as ET

ROOT = Path(r"c:\Users\Admin\Downloads\TOOL_V4")
NS = {"w": "http://schemas.openxmlformats.org/wordprocessingml/2006/main"}


def cell_text(cell) -> str:
    parts = []
    for t in cell.findall(".//w:t", NS):
        if t.text:
            parts.append(t.text)
    return "".join(parts).replace("\n", " ").strip()


def analyze(path: Path) -> None:
    with zipfile.ZipFile(path) as z:
        xml = z.read("word/document.xml").decode("utf-8")
    has_ph = "{{TABLE_PLACEHOLDER}}" in xml
    root_xml = ET.fromstring(xml)
    tables = root_xml.findall(".//w:tbl", NS)
    print("===", path.name, "===")
    print("TABLE_PLACEHOLDER", has_ph, "tables", len(tables))
    if not tables:
        return
    rows = tables[0].findall("./w:tr", NS)
    print("rows", len(rows))
    for i, row in enumerate(rows):
        cells = row.findall("./w:tc", NS)
        c0 = cell_text(cells[0]) if cells else ""
        c1 = cell_text(cells[1])[:50] if len(cells) > 1 else ""
        if i < 4 or i >= len(rows) - 2:
            print(f"  row{i} ncells={len(cells)} stt={c0!r} name={c1!r}")
        elif i == 4:
            print("  ...")


def main() -> None:
    cands = [p for p in ROOT.rglob("99-*.docx") if "sua" in str(p).lower()]
    if not cands:
        cands = list(ROOT.rglob("99-*.docx"))
    seen = set()
    for p in cands:
        if p.name in seen:
            continue
        seen.add(p.name)
        analyze(p)


if __name__ == "__main__":
    main()
