@echo off
chcp 65001 >nul
setlocal

set "SRC=F:\TOOL - HOP DONG\ContractToolData"
if not exist "%SRC%\" set "SRC=Z:\TOOL - HOP DONG\ContractToolData"
set "REMOTE=gdrive:ContractToolBackup"

for /f %%I in ('powershell -NoProfile -Command "Get-Date -Format yyyy-MM-dd"') do set "DAY=%%I"

if not exist "%SRC%\" (
  echo [LOI] Khong thay: %SRC%
  pause & exit /b 1
)

where rclone >nul 2>&1 || (
  echo [LOI] Chua co rclone. Cai: winget install Rclone.Rclone
  echo Roi chay: rclone config  ^(remote ten: gdrive^)
  pause & exit /b 1
)

echo.
echo === BACKUP -^> Drive / ContractToolBackup / %DAY% ===
echo Nguon: %SRC%
echo.

rclone mkdir "%REMOTE%/%DAY%" 2>nul
rclone copy "%SRC%" "%REMOTE%/%DAY%" -v --exclude "*.tmp" || (
  echo [LOI] rclone that bai. Kiem tra: rclone config
  pause & exit /b 1
)

rem 3 file goc nam canh thu muc ngay, trong ContractToolBackup
for %%F in (bang_mau.json hang_hoa_bac_nam.json khach_hang.json) do (
  if exist "%SRC%\%%F" rclone copy "%SRC%\%%F" "%REMOTE%/" -q
)

echo.
echo [OK] Da upload.
endlocal
