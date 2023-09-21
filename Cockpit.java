import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.lang.*;
import java.util.*;
import java.text.DecimalFormat;
import javax.sound.sampled.*;
import java.io.*;

/** Cockpit.java
* @author Lucca DiMario
* @since 23 May 2022
* Creates the outline for an interactive plane cockpit
*/
public class Cockpit{
    /**
    * menu bar fields for the creation of the options menu
    */
    private JMenuBar bar;
    private JMenu menu;
    private JCheckBoxMenuItem showCraftInfoCB, showWeatherInfoCB;
    /**
    * the main frame that will hold the main JPanel
    */
    private JFrame frame;
    /**
    * the main JPanel
    */
    private JPanel cockpit;
    /** 
    * button that handles the engine being on or off
    */
    private JButton keyStart;
    /**
    *Image backround of the jpanel
    */
    private Image background;
    /** slider for controlling thrust output
    */
    private JSlider slider;
    /**
    * int variable for holding the angleOfAttack of the aircraft
    */
    private int angleOfAttack = 0;
    /**
    * booleans that control wheter or not the engine is on, and the info checkboxes
    */
    private boolean engineOn, showCraftInfo, showWeatherInfo = false;
    /**
    * JLabels that display information about the plane, system, and what the plane is doing
    */
    private JLabel aircraftInfo, weatherInfo, velocityInfo, angleDisplay, controlsInfo, onIndicator, thrustDisplay, altimiter, stallWarning, sinkRateWarning;
    /**
    * final variable for the weight of the plane
    */
    private final double WEIGHT = 3000;
    /**
    * final variable for the wingspan of the plane
    */
    private final double WINGSPAN = 13.41;
    /**
    * final variable for the wing area of the plane
    */
    private final double WING_AREA = 78.03;
    /**
    * final variable for the air density of the surrounding atmosphere
    */
    private final double AIR_DENSITY = 1.225;
    /**
    * final variable for the drag coefficient of the plane
    */
    private final double DRAG_COEFFICIENT = .0292;
    /**
    * final variable for the max speed of the plane
    */
    private final double MAX_SPEED = 5000.0;
    /**
    * variable for the velocity of the plane
    */
    private double velocity = 0.0;
    /**
    * final variable for the value used to switch knots to feet/min
    */
    private final double TO_FPM_VAL = 101.269;
    /**
    * final variable for the terminal vertical velocity of the plane
    */
    private final double TERMINAL_VELOCITY = -145.17;
    /**
    * variables to keep track of the vertical and horizontal velocity of the plane
    */
    private double verticalVel, horizontalVel = 0.0;
    /**
    * variable to keep track of the percent thrust the plane is outputting
    */
    private int thrust;
    /**
    * object that formats the decimal variables to two places in text
    */
    private DecimalFormat twoPlaces = new DecimalFormat("#.##");
    /**
    * varibles to keep track of the vertical and horizontal acceleration of the plane
    */
    private double vertAccel, horAccel;
    /**
    * final variable for the maximum acceleration of the plane
    */
    private final double MAX_PLANE_ACCEL = 20.604;
    /**
    * variable to keep track of the acceleration of the plane
    */
    private double accel;
    /**
    * variable to keep track of the altitude of the airplane
    */
    private double altitude;
    /**
    * final variable for the meters to feet conversion
    */
    private final double METER_TO_FEET_VALUE = 3.28084;
    /**
    * final variable to the horizontal decelleration when the plane is off
    */
    private final double AIR_RESISTANCE_DECCEL = -2.0;
    /**
    * final variable for the proper width of a cockpit window
    */
    private final int COCKPIT_WIDTH = 1280;
    /**
    * final variable for the proper height of a cockpit height
    */
    private final int COCKPIT_HEIGHT = 600;



    /**
    * constructor for the cockpit object
    */
    public Cockpit()
    {
        //initializes the fonts to be used with the jLabels
        Font font = new Font("Font1", 1, 15);
        Font font2 = new Font("Font2", 1, 10);
        ImageIcon icon = new ImageIcon("Glider.png");//image icon for backround
        Image background = icon.getImage(); //overrides the paint component method which draws on the backround of the jpanel
        cockpit = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(background, 0, 0, COCKPIT_WIDTH, COCKPIT_HEIGHT, this); //draws for correct width and height for the components
            }
        };
        cockpit.setSize(COCKPIT_WIDTH, COCKPIT_HEIGHT);
        cockpit.setLayout(null);//sets jpanel layout to null

        bar = new JMenuBar();//initalizes the menuBar to hold the options menu
        menu = new JMenu("Options");//initalizes the options menu
        showCraftInfoCB = new JCheckBoxMenuItem("Show Aircraft Info"); //menu + check boxes creation
        showWeatherInfoCB = new JCheckBoxMenuItem("Show Weather Info");
        showCraftInfoCB.addItemListener(new showCraftInfoItemListener());
        showWeatherInfoCB.addItemListener(new showWeatherInfoItemListener());
        menu.add(showWeatherInfoCB);
        menu.add(showCraftInfoCB);
        bar.add(menu);

        frame = new JFrame(); // frame creation
        frame.setJMenuBar(bar); //adding menu bar

        slider = new JSlider(JSlider.VERTICAL, 0, 100, 0); //slider creaiton
        Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>(); //hashtable for slider
        table.put (0, new JLabel(new ImageIcon("yourFile.gif")));
        JLabel idle = new JLabel("IDLE"); //labels for slider
        idle.setForeground(Color.WHITE);
        idle.setFont(font2);
        table.put (0, idle);
        JLabel fullThrust = new JLabel("FULL THRUST");
        fullThrust.setForeground(Color.WHITE);
        fullThrust.setFont(font2);
        table.put (100, fullThrust);
        //sets the tickmarks and specifications for the JSlider
        slider.setLabelTable(table);
        slider.setPaintTicks(true);
        slider.setMinorTickSpacing(5);
        slider.setMajorTickSpacing(20);
        slider.setPaintLabels(true);
        slider.setBackground(Color.GRAY);
        slider.setForeground(Color.red);
        slider.setBounds(640,500,100,100);
        slider.setOpaque(false);

        ImageIcon start = new ImageIcon("start.png");//start button picture creation
        Image image1 = start.getImage();
        Image newimg1 = image1.getScaledInstance(30,30, java.awt.Image.SCALE_SMOOTH);//scaled to a good button size
        start = new ImageIcon(newimg1);
        keyStart = new JButton(start);
        keyStart.setBorder(null);
        keyStart.setBackground(Color.BLACK);
        keyStart.setOpaque(false);
        keyStart.setBounds(332,550,30,30);

        onIndicator = new JLabel("ENGINE OFF"); //indicator for if the engine is on or not
        onIndicator.setForeground(Color.RED);
        onIndicator.setBounds(640,320,100,100);
        onIndicator.setFont(font2);

        controlsInfo = new JLabel("<html>Press the POWER BUTTON on the plane cockpit to start your engine; turns the program on and off<br>Use SLIDER or UP/DOWN ARROW KEYS for thrust output<br>Use W KEY and S KEY for pitch down and up respectively<html>"); //controls jlabel
        controlsInfo.setForeground(Color.WHITE);
        controlsInfo.setBounds(0,0,300,400);
        controlsInfo.setFont(font2);

        stallWarning = new JLabel("STALL WARNING"); //stall warning indicator creation
        stallWarning.setForeground(Color.WHITE);
        stallWarning.setFont(font2);
        stallWarning.setBounds(470,400,100,100);

        sinkRateWarning = new JLabel("SINK RATE"); //sink rate indicator creation.
        sinkRateWarning.setForeground(Color.WHITE);
        sinkRateWarning.setFont(font2);
        sinkRateWarning.setBounds(800,400,100,100);

        angleDisplay = new JLabel("<html>ATTITUDE<br>INDICATOR:<br>" + angleOfAttack + "º<html>"); //display for the attitude of the plane
        angleDisplay.setForeground(Color.WHITE);
        angleDisplay.setBounds(470,440,100,100);
        angleDisplay.setFont(font2);

        thrustDisplay = new JLabel("<html>THRUST<br>OUTPUT:<br>" + thrust + "%<html>"); //display for the amount of thrust of the plane
        thrustDisplay.setForeground(Color.WHITE);
        thrustDisplay.setBounds(565,400,120,320);
        thrustDisplay.setFont(font);

        velocityInfo = new JLabel("<html>VELOCITY INFORMATION PANEL:<br>Speed: " + twoPlaces.format(velocity) + "KT<br>Ground Speed: " + twoPlaces.format(horizontalVel) + "KT<br>Vertical Speed: " + twoPlaces.format(verticalVel) + "f/m"); //display for the velocity information of the plane
        velocityInfo.setFont(font2);
        velocityInfo.setBounds(600,430,350,50);
        velocityInfo.setForeground(Color.WHITE);

        altimiter = new JLabel("<html>ALTIMETER:<br>" + twoPlaces.format((altitude*METER_TO_FEET_VALUE)) + "ft");//display for the altitude of the plane
        altimiter.setFont(font2);
        altimiter.setForeground(Color.WHITE);
        altimiter.setBounds(800,460,100,50);

        aircraftInfo = new JLabel();//label that displays aircraft info when options checkbox is checked
        aircraftInfo.setFont(font2);
        aircraftInfo.setText("<html>Aircraft Name: Glider With Wings Mark 23; Turbojet<br>Aircraft Max Speed: " + MAX_SPEED + "KT<br>Aircraft Drag Coefficient: .0292<br>Aircraft Weight: " + WEIGHT + "kg<br>Wing Span: "+ WINGSPAN + "m<br>Wing Area: " + WING_AREA + "m^2 <html>");
        aircraftInfo.setBounds(0,0,350,50);
        aircraftInfo.setForeground(Color.WHITE);


        weatherInfo = new JLabel();//label that displays the weather info when the weather checkbox is checked
        weatherInfo.setFont(font2);
        weatherInfo.setText("<html>Visibility: 0SM<br>Air Density: 1.225kg/m^3<br>Clouds: OVC007<br>Wind: 19001KT<html>");
        weatherInfo.setBounds(0,60,350,50);
        weatherInfo.setForeground(Color.WHITE);

        //adding listeners to components
        slider.addKeyListener(new thetaListener());
        thrustDisplay.addKeyListener(new thetaListener());
        angleDisplay.addKeyListener(new thetaListener());
        keyStart.addKeyListener(new thetaListener());
        keyStart.addActionListener(new startListener());
        slider.addChangeListener(new thrustListener());

        //adding everything to jPanel
        cockpit.add(angleDisplay);
        cockpit.add(thrustDisplay);
        cockpit.add(keyStart);
        cockpit.add(slider);
        cockpit.add(controlsInfo);
        cockpit.add(velocityInfo);
        cockpit.add(altimiter);
        cockpit.add(stallWarning);
        cockpit.add(sinkRateWarning);
        cockpit.add(onIndicator);

        //adding panel to frame
        frame.add(cockpit);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(0,0,COCKPIT_WIDTH,COCKPIT_HEIGHT + 60);//accounts for menu bar size
        frame.setResizable(false);
        frame.setVisible(true);

        new Thread(() -> {
            updateVelocityAndAltitude();//starts new thread to constantly update the velocity and altitude of the plane
        }).start();

    }
    /**
    * class that listens to and changes the thrust output of the airplane
    */
    private class thrustListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            if(engineOn)
                thrust = slider.getValue(); //only changes the thrust if the engine is on
            else
                thrust = 0;
            updateAccel(); //updates acceleration(vertical and horizontal) for the new thrust
            thrustDisplay.setText("<html>THRUST<br>OUTPUT:<br>" + thrust + "%<html>"); //updates thrust display
            horizontalVel = getHorizontal(velocity); //updates horizontal velocity
            verticalVel = getVertical(velocity); //updates vertical velocity
            velocityInfo.setText("<html>VELOCITY INFORMATION PANEL:<br>Speed: " + twoPlaces.format(velocity) + "KT<br>Ground Speed: " + twoPlaces.format(horizontalVel) + "KT<br>Vertical Speed: " + twoPlaces.format(toFPM(verticalVel)) + "f/m"); //updates the velocity display



        }
    }
    /**
    *class that listens to the start button
    */
    private class startListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            engineOn = !engineOn; //changes the variable engine on to true or false
            if(engineOn)
            {
                thrust = slider.getValue(); //reupdates all variables and displays when the engine comes on
                updateAccel();
                thrustDisplay.setText("<html>THRUST<br>OUTPUT:<br>" + thrust + "%<html>");
                horizontalVel = getHorizontal(velocity);
                verticalVel = getVertical(velocity);
                velocityInfo.setText("<html>VELOCITY INFORMATION PANEL:<br>Speed: " + twoPlaces.format(velocity) + "KT<br>Ground Speed: " + twoPlaces.format(horizontalVel) + "KT<br>Vertical Speed: " + twoPlaces.format(toFPM(verticalVel)) + "f/m");
                onIndicator.setText("ENGINE ON"); //sets display to engine on
                onIndicator.setForeground(Color.GREEN);
            }
            else {
                thrust = 0; //if the engine is not on it sets thrust to 0 and sets the display to engine off
                thrustDisplay.setText("<html>THRUST<br>OUTPUT:<br>" + thrust + "%<html>");
                onIndicator.setText("ENGINE OFF");
                onIndicator.setForeground(Color.RED);
            }
        }
    }
    /**
    * class that listens to the the keys to pitch the plane up or down
    */
    private class thetaListener implements KeyListener
    {
        public void keyPressed(KeyEvent e)
        {
            if(e.getKeyCode() == KeyEvent.VK_S) //if key pressed increases the angle of attack
            {
                if(angleOfAttack < 90)//makes sure the angle of attack stays <= to 90
                {
                    angleDisplay.setText("<html>ATTITUDE<br>INDICATOR:<br>" + (++angleOfAttack) + "º<html>");
                }
            }
            else if(e.getKeyCode() == KeyEvent.VK_W) //if key pressed decreases the angle of attack
            {
                if(angleOfAttack > -90)//makes sure the angle of attack stays >= -90
                {
                    angleDisplay.setText("<html>ATTITUDE<br>INDICATOR:<br>" + (--angleOfAttack) + "º<html>");
                }

            }
            updateAccel(); //updates acceleration with new angle
            horizontalVel = getHorizontal(velocity); //updates horizontal velocity
            verticalVel = getVertical(velocity); //updates vertical velocity
            velocityInfo.setText("<html>VELOCITY INFORMATION PANEL:<br>Speed: " + twoPlaces.format(velocity) + "KT<br>Ground Speed: " + twoPlaces.format(horizontalVel) + "KT<br>Vertical Speed: " + twoPlaces.format(toFPM(verticalVel)) + "f/m"); //updates velocity info panel
        }
        public void keyReleased(KeyEvent e)
        {

        }
        public void keyTyped(KeyEvent e)
        {

        }
    }
    /**
    * handles the checkbox to show aircraft info
    */
    private class showCraftInfoItemListener implements ItemListener
    {
        public void itemStateChanged(ItemEvent e)
        {
            showCraftInfo = !showCraftInfo;
            if(showCraftInfo) //if the box is checked it shows the info
            {
                cockpit.add(aircraftInfo);
            }
            else //if box is unchecked it removes info
            {
                cockpit.remove(aircraftInfo);
            }
            cockpit.revalidate(); //updates the panel
            cockpit.repaint(); //updates the panel
        }
    }
    /**
    * handles the checkbox to show weather info
    */
    private class showWeatherInfoItemListener implements ItemListener
    {
        public void itemStateChanged(ItemEvent e)
        {
            showWeatherInfo = !showWeatherInfo;
            if(showWeatherInfo) //if box is checked, displays weather info
            {
                cockpit.add(weatherInfo);
            }
            else //if box is unchecked, removes the weather info
            {
                cockpit.remove(weatherInfo);
            }
            cockpit.revalidate(); //updates the panel
            cockpit.repaint(); //updates the panel
        }
    }
    /**
    * uses sin function to get the vertical component of velocity or acceleration
    */
    public double getVertical(double vel)
    {
        return((double)vel * Math.sin(angleOfAttack * Math.PI / 180));

    }
    /**
    * uses cos function to get the horizontal component of velocity or acceleration
    */
    public double getHorizontal(double vel)
    {
        return(((double)vel*Math.cos(angleOfAttack * Math.PI / 180)));
    }
    /**
    * updates accel based on thrust and angle of attack
    */
    public void updateAccel()
    {
        accel = ((double)thrust)/100*MAX_PLANE_ACCEL; //calculates the acceleration based on thrust
        vertAccel = getVertical(accel); //calculates vertical accel based on accel and angle of attack
        horAccel = getHorizontal(accel); // calculates horizontal accel based on accel and angle of attack
        if(angleOfAttack > 0) {
            vertAccel = vertAccel - ((((double)angleOfAttack)/10) *2.6); //handles the decrease of acceleration based on angle up of the airplane
        }
        else if(angleOfAttack < 0) {
            vertAccel = vertAccel + ((((double)angleOfAttack)/10) *2.6); //handles the increase of acceleration based on the angle down of the airplane
        }
    }
    /**
    * multiplies any value by the value to change knots to feet per minute.
    */
    public double toFPM(double val)
    {
        return (val*TO_FPM_VAL);
    }
    /**
    * method that is run in a thread to update the velocity and altitude of the plane every second
    */
    public void updateVelocityAndAltitude()
    {
        double convertedAlt;
        while(true) {
            if(engineOn) { //only run these calculations if the engine is on
                sinkRateWarning.setForeground(Color.WHITE);
                stallWarning.setForeground(Color.WHITE);
                if (velocity < ((double) thrust) / 100 * MAX_SPEED && verticalVel > TERMINAL_VELOCITY) { //makes sure the plane doesnt accelerate faster than it should be going
                    //constantly adds the acceleration to the velocity
                    horizontalVel += horAccel;
                    verticalVel += vertAccel;
                    velocity = Math.sqrt(Math.pow(horizontalVel, 2) + Math.pow(verticalVel, 2));
                    velocityInfo.setText("<html>VELOCITY INFORMATION PANEL:<br>Speed: " + twoPlaces.format(velocity) + "KT<br>Ground Speed: " + twoPlaces.format(horizontalVel) + "KT<br>Vertical Speed: " + twoPlaces.format(toFPM(verticalVel)) + "f/m");
                }
                if(verticalVel < TERMINAL_VELOCITY) { //makes sure the plane does not go faster than terminal velocity
                    verticalVel = TERMINAL_VELOCITY;
                    velocity = Math.sqrt(Math.pow(horizontalVel, 2) + Math.pow(verticalVel, 2));
                    velocityInfo.setText("<html>VELOCITY INFORMATION PANEL:<br>Speed: " + twoPlaces.format(velocity) + "KT<br>Ground Speed: " + twoPlaces.format(horizontalVel) + "KT<br>Vertical Speed: " + twoPlaces.format(toFPM(verticalVel)) + "f/m");
                }
                if(horAccel == 0) { //deccelerates the plane if the thrust is set to 0(air resistance)
                    if(horizontalVel > 0) {
                        horizontalVel += AIR_RESISTANCE_DECCEL;
                    }
                    velocity = Math.sqrt(Math.pow(horizontalVel, 2) + Math.pow(verticalVel, 2));
                    velocityInfo.setText("<html>VELOCITY INFORMATION PANEL:<br>Speed: " + twoPlaces.format(velocity) + "KT<br>Ground Speed: " + twoPlaces.format(horizontalVel) + "KT<br>Vertical Speed: " + twoPlaces.format(toFPM(verticalVel)) + "f/m");

                }
                altitude += verticalVel;
                convertedAlt = altitude * METER_TO_FEET_VALUE;
                if(convertedAlt < 0) {
                    altimiter.setText("<html>ALTIMETER:<br>" + (twoPlaces.format(convertedAlt)) + "ft<br>(YOU ARE DEAD)<html>");
                }
                else {
                    altimiter.setText("<html>ALTIMETER:<br>" + (twoPlaces.format(convertedAlt)) + "ft<htm;>");
                }
                
                if (angleOfAttack < -60) {
                    sinkRateWarning.setForeground(Color.RED); //flashes sink rate warning
                }
                if (angleOfAttack > 60) {
                    stallWarning.setForeground(Color.RED); //flashes the stall warning
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
            }
            else { //if engine is off
                //resets the warnings
                sinkRateWarning.setForeground(Color.WHITE); 
                stallWarning.setForeground(Color.WHITE);
                //sets values to 0 
                velocity = 0.0;
                horizontalVel = 0.0;
                verticalVel = 0.0;
                altitude = 0.0;
                //updates the velocity and altimiter displays to show 0;
                velocityInfo.setText("<html>VELOCITY INFORMATION PANEL:<br>Speed: " + twoPlaces.format(velocity) + "KT<br>Ground Speed: " + twoPlaces.format(horizontalVel) + "KT<br>Vertical Speed: " + twoPlaces.format(toFPM(verticalVel)) + "f/m");
                altimiter.setText("<html>ALTIMETER:<br>" + (twoPlaces.format(altitude)) + "ft<html>");
            }
        }

    }

    public static void main(String[] args)  {
        Cockpit plane = new Cockpit();//initializes new cockpit object




    }

}
