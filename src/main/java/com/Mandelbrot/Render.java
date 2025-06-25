package com.Mandelbrot;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class Render {
    private Properties properties = new Properties();
    private FileReader fr;
    private FileWriter fw;
    private char inside;
    private char outside;
    private File Config = new File("Mandelbrot config");
    private Terminal terminal;
    private char[][] Buffer;
    private int iterations=1000;
    private double deltaX;
    private double deltaY;
    private double startX = -2;
    private double endX = 1;
    private double startY = 1.12;
    private double endY = -1.2;
    public Render(){
        try {
            ReadConfig();
            terminal = TerminalBuilder.builder().system(true).build();
            Buffer = new char[terminal.getHeight()][terminal.getWidth()];
            deltaX = (Math.abs(startX)+Math.abs(endX))/terminal.getWidth();
            deltaY = (Math.abs(startY)+Math.abs(endY))/terminal.getHeight();
        } catch (Exception e) {
        }
        Run();
    }
    private void ReadConfig(){
        if(!Config.exists()){
            CreatNewConfig();
        }else{
            try {
                fr = new FileReader(Config);
                properties.load(fr);
            } catch (IOException e) {}
            startX = Double.parseDouble(properties.getProperty("startX"));
            endX = Double.parseDouble(properties.getProperty("endX"));
            startY = Double.parseDouble(properties.getProperty("startY"));
            endY = Double.parseDouble(properties.getProperty("endY"));
            iterations = Integer.parseInt(properties.getProperty("iterations"));
            inside = properties.getProperty("inside").charAt(0);
            outside = properties.getProperty("outside").charAt(0);
        }
    }
    private void CreatNewConfig(){
        try {
            Config.createNewFile();
            fw = new FileWriter(Config);
            properties.setProperty("startX", "-2");
            properties.setProperty("endX", "1");
            properties.setProperty("startY", "1.2");
            properties.setProperty("endY", "-1.2");
            properties.setProperty("iterations", "1000");
            properties.setProperty("inside", "#");
            properties.setProperty("outside", " ");
            properties.store(fw, "Mandelbrot config");
            fw.close();
            ReadConfig();
        } catch (Exception e) {}
    }
    private void Run(){
        int x=0;
        int y=terminal.getHeight()-1;
        
        while(startX<endX){
            while(startY>endY){
                if(y==0){
                    break;
                }
                if(x==Buffer[0].length){
                    break;
                }
                if(CalcInterations()){
                    Buffer[y][x] = inside;
                }else{
                    Buffer[y][x] = outside;
                }
                startY -= deltaY;
                y--;
            }
            startX += deltaX;
            startY = 1.12;
            y = terminal.getHeight()-1;
            x++;
        }
        Draw();
    }
    private boolean CalcInterations(){
        Complex Add = new Complex(startX, startY);
        Complex Complex = new Complex(0,0);
        for(int k=0;k<iterations;k++){
            Complex = Complex.times(Complex);
            Complex = Complex.plus(Add);
            if(Complex.abs()>2){
                return false;
            }
        }
        return true;
    }
    private void Draw(){
        String Draw = "";
        for(int y=0;y<Buffer.length;y++){
            for(int x=0;x<Buffer[0].length;x++){
                Draw += Buffer[y][x];
            }
            Draw += System.lineSeparator();
        }
        System.out.println(Draw);
    }
    public static void main(String[] args) {
        new Render();
    }
}
