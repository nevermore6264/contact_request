# -*- coding: utf-8 -*-
import zipfile
import re
from pathlib import Path

ROOT = Path(r"c:\Users\Admin\Downloads\TOOL_V4")


def main() -> None:
    cands = [p for p in ROOT.rglob("99-3*.docx") if "sua" in str(p).lower()]
    if not cands:
        cands = list(ROOT.rglob("99-3*.docx"))
    p = cands[0]
    with zipfile.ZipFile(p) as z:
        xml = z.read("word/document.xml").decode("utf-8")
    print("file", p.name)
    print("placeholder count", xml.count("{{TABLE_PLACEHOLDER}}"))
    # show context around placeholder
    idx = xml.find("TABLE_PLACEHOLDER")
    if idx >= 0:
        print("context:", xml[max(0, idx - 200) : idx + 200].replace("\n", " "))
    # body children order: p / tbl
    body = re.search(r"<w:body[^>]*>(.*)</w:body>", xml, re.S)
    if not body:
        print("no body")
        return
    inner = body.group(1)
    parts = re.findall(r"<(w:p|w:tbl)\b", inner)
    print("body elements", len(parts), "first20", parts[:20])
    for i, tag in enumerate(parts):
        if tag == "w:tbl":
            print(" table at", i)
        # find placeholder paragraph index
    for i, m in enumerate(re.finditer(r"<w:p[\s>].*?</w:p>", inner, re.S)):
        if "TABLE_PLACEHOLDER" in m.group(0):
            print("placeholder in paragraph index among p-only? scan body order")
            break
    # find index in parts
    pos = 0
    for i, tag in enumerate(parts):
        if tag == "w:p":
            # get next p chunk roughly
            pass
    # simpler: split by major tags
    chunks = re.split(r"(?=<w:(?:p|tbl)\b)", inner)
    for i, ch in enumerate(chunks):
        if not ch.strip():
            continue
        kind = "tbl" if ch.startswith("<w:tbl") else ("p" if ch.startswith("<w:p") else "?")
        has = "TABLE_PLACEHOLDER" in ch
        if has or kind == "tbl" or i < 8:
            text = re.sub(r"<[^>]+>", "", ch)
            text = re.sub(r"\s+", " ", text).strip()[:80]
            print(f"[{i}] {kind} ph={has} text={text!r}")


if __name__ == "__main__":
    main()
