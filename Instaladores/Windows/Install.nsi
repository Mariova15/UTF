# Idiomas

# Nombre del instalador
Name "Use that font"

# The file to write
OutFile "Install.exe"

# The default installation directory
InstallDir $PROGRAMFILES\UTF

# Pedimos permisos para Windows 7
RequestExecutionLevel admin

# Pantallas que hay que mostrar del instalador

Page directory
Page instfiles

#Cambiar el idioma
!include "MUI.nsh"
!insertmacro MUI_LANGUAGE "Spanish"


#Seccion principal
Section

  # Establecemos el directorio de salida al directorio de instalacion
  SetOutPath $INSTDIR
  
  # Creamos el desinstalador
  writeUninstaller "$INSTDIR\uninstall.exe"
  
  # Ponemos ahi el archivo test.txt
  File "UTF.exe"
  File /r "lib"

  # Se crea un  nuevo acceso directo en el menu de inicio. Como le pasamos
  # en el segundo parametro vacio, no llama a nada de momento
  createShortCut "$SMPROGRAMS\Use that font.lnk" "$INSTDIR\UTF.exe"
  createShortCut "$DESKTOP\Use that font.lnk" "$INSTDIR\UTF.exe"

  #Añadimos información para que salga en el menú de desinstalar de Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\UTF" \
                 "DisplayName" "Use that font"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\UTF" \
                 "Publisher" "Mario - Desarrollo de aplicaciones multiplataforma"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\UTF" \
                 "UninstallString" "$\"$INSTDIR\uninstall.exe$\""
  
  
# Fin de la seccion
SectionEnd

# seccion del desintalador
section "uninstall"
 
    # borramos el desintalador primero
    delete "$INSTDIR\uninstall.exe"
 
    # borramos el acceso directo del menu de inicio
    delete "$INSTDIR\UTF.exe"
    RmDir /r "$INSTDIR\lib"
    RmDir /r "$INSTDIR\Datos"
    RmDir /r "$INSTDIR\Backup"
    RmDir /r "$INSTDIR\Mis Fuentes"
    delete "$SMPROGRAMS\Use that font.lnk"
    delete "$DESKTOP\Use that font.lnk"
	
    RmDir "$INSTDIR"

    #Borramos la entrada del registro
    DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\UTF"
 
# fin de la seccion del desinstalador
sectionEnd
