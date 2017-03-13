#!/bin/bash

exportFile() {
    RES_ROOT="../app/src/main/res/$1"
    MUL=$2

    size_m=`echo $MUL \* 48 | bc -l`
    size_h=`echo $MUL \* 72 | bc -l`
    size_xh=`echo $MUL \* 96 | bc -l`
    size_xxh=`echo $MUL \* 144 | bc -l`
    size_xxxh=`echo $MUL \* 192 | bc -l`

    echo "-- Exporting $4"
    inkscape -f $3 -e ${RES_ROOT}-mdpi/$4.png -C -w $size_m
    inkscape -f $3 -e ${RES_ROOT}-hdpi/$4.png -C -w $size_h
    inkscape -f $3 -e ${RES_ROOT}-xhdpi/$4.png -C -w $size_xh
    inkscape -f $3 -e ${RES_ROOT}-xxhdpi/$4.png -C -w $size_xxh
    inkscape -f $3 -e ${RES_ROOT}-xxxhdpi/$4.png -C -w $size_xxxh
}


exportFile "mipmap" 1.0 icon.svg ic_launcher
exportFile "drawable" 3.0 icon.svg about
