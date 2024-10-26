# build.ps1 - PowerShell version of build.sh
# Usage: At src directory, type ./build.ps1

Write-Output "Compiling interface classes..."
javac interfaces\*.java
if ($?) {
    Write-Output "Interface classes compiled successfully."
} else {
    Write-Output "Failed to compile interface classes."
    exit 1
}

Write-Output "Compiling utility classes..."
javac utility\*.java
if ($?) {
    Write-Output "Utility classes compiled successfully."
} else {
    Write-Output "Failed to compile utility classes."
    exit 1
}

Write-Output "Compiling entity classes..."
javac entity\*.java
if ($?) {
    Write-Output "Entity classes compiled successfully."
} else {
    Write-Output "Failed to compile entity classes."
    exit 1
}

Write-Output "Compiling boundary classes..."
javac boundary\*.java
if ($?) {
    Write-Output "Boundary classes compiled successfully."
} else {
    Write-Output "Failed to compile coundary classes."
    exit 1
}

Write-Output "Compiling controller classes..."
javac controller\*.java
if ($?) {
    Write-Output "Controller classes compiled successfully."
} else {
    Write-Output "Failed to compile controller classes."
    exit 1
}

Write-Output "Compiling main class..."
javac *.java
if ($?) {
    Write-Output "Main class compiled successfully."
} else {
    Write-Output "Failed to compile Main class."
    exit 1
}

Write-Output "Compiling main class..."
java HospitalManagementSystem
if (!$?) {
    Write-Output "Failed to run Main class."
    exit 1
}