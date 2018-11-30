package de.uniks.liverisk.model;

import java.beans.PropertyChangeSupport;

import java.beans.PropertyChangeListener;

public class Platform 
{

   public static final String PROPERTY_capacity = "capacity";

   private int capacity;

   public int getCapacity()
   {
      return capacity;
   }

   public Platform setCapacity(int value)
   {
      if (value != this.capacity)
      {
         int oldValue = this.capacity;
         this.capacity = value;
         firePropertyChange("capacity", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_xPos = "xPos";

   private double xPos;

   public double getXPos()
   {
      return xPos;
   }

   public Platform setXPos(double value)
   {
      if (value != this.xPos)
      {
         double oldValue = this.xPos;
         this.xPos = value;
         firePropertyChange("xPos", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_yPos = "yPos";

   private double yPos;

   public double getYPos()
   {
      return yPos;
   }

   public Platform setYPos(double value)
   {
      if (value != this.yPos)
      {
         double oldValue = this.yPos;
         this.yPos = value;
         firePropertyChange("yPos", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_game = "game";

   private Game game = null;

   public Game getGame()
   {
      return this.game;
   }

   public Platform setGame(Game value)
   {
      if (this.game != value)
      {
         Game oldValue = this.game;
         if (this.game != null)
         {
            this.game = null;
            oldValue.withoutPlatforms(this);
         }
         this.game = value;
         if (value != null)
         {
            value.withPlatforms(this);
         }
         firePropertyChange("game", oldValue, value);
      }
      return this;
   }



   public static final String PROPERTY_selectedBy = "selectedBy";

   private Game selectedBy = null;

   public Game getSelectedBy()
   {
      return this.selectedBy;
   }

   public Platform setSelectedBy(Game value)
   {
      if (this.selectedBy != value)
      {
         Game oldValue = this.selectedBy;
         if (this.selectedBy != null)
         {
            this.selectedBy = null;
            oldValue.setSelectedPlatform(null);
         }
         this.selectedBy = value;
         if (value != null)
         {
            value.setSelectedPlatform(this);
         }
         firePropertyChange("selectedBy", oldValue, value);
      }
      return this;
   }



public static final java.util.ArrayList<Platform> EMPTY_neighbors = new java.util.ArrayList<Platform>()
   { @Override public boolean add(Platform value){ throw new UnsupportedOperationException("No direct add! Use xy.withNeighbors(obj)"); }};


public static final String PROPERTY_neighbors = "neighbors";

private java.util.ArrayList<Platform> neighbors = null;

public java.util.ArrayList<Platform> getNeighbors()
   {
      if (this.neighbors == null)
      {
         return EMPTY_neighbors;
      }

      return this.neighbors;
   }

public Platform withNeighbors(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withNeighbors(i);
            }
         }
         else if (item instanceof Platform)
         {
            if (this.neighbors == null)
            {
               this.neighbors = new java.util.ArrayList<Platform>();
            }
            if ( ! this.neighbors.contains(item))
            {
               this.neighbors.add((Platform)item);
               ((Platform)item).withNeighbors(this);
               firePropertyChange("neighbors", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }


public Platform withoutNeighbors(Object... value)
   {
      if (this.neighbors == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutNeighbors(i);
            }
         }
         else if (item instanceof Platform)
         {
            if (this.neighbors.contains(item))
            {
               this.neighbors.remove((Platform)item);
               ((Platform)item).withoutNeighbors(this);
               firePropertyChange("neighbors", item, null);
            }
         }
      }
      return this;
   }


   public static final String PROPERTY_player = "player";

   private Player player = null;

   public Player getPlayer()
   {
      return this.player;
   }

   public Platform setPlayer(Player value)
   {
      if (this.player != value)
      {
         Player oldValue = this.player;
         if (this.player != null)
         {
            this.player = null;
            oldValue.withoutPlatforms(this);
         }
         this.player = value;
         if (value != null)
         {
            value.withPlatforms(this);
         }
         firePropertyChange("player", oldValue, value);
      }
      return this;
   }



   public static final java.util.ArrayList<Unit> EMPTY_units = new java.util.ArrayList<Unit>()
   { @Override public boolean add(Unit value){ throw new UnsupportedOperationException("No direct add! Use xy.withUnits(obj)"); }};


   public static final String PROPERTY_units = "units";

   private java.util.ArrayList<Unit> units = null;

   public java.util.ArrayList<Unit> getUnits()
   {
      if (this.units == null)
      {
         return EMPTY_units;
      }

      return this.units;
   }

   public Platform withUnits(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withUnits(i);
            }
         }
         else if (item instanceof Unit)
         {
            if (this.units == null)
            {
               this.units = new java.util.ArrayList<Unit>();
            }
            if ( ! this.units.contains(item))
            {
               this.units.add((Unit)item);
               ((Unit)item).setPlatform(this);
               firePropertyChange("units", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }



   public Platform withoutUnits(Object... value)
   {
      if (this.units == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutUnits(i);
            }
         }
         else if (item instanceof Unit)
         {
            if (this.units.contains(item))
            {
               this.units.remove((Unit)item);
               ((Unit)item).setPlatform(null);
               firePropertyChange("units", item, null);
            }
         }
      }
      return this;
   }


   protected PropertyChangeSupport listeners = null;

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (listeners != null)
      {
         listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   public boolean addPropertyChangeListener(PropertyChangeListener listener)
   {
      if (listeners == null)
      {
         listeners = new PropertyChangeSupport(this);
      }
      listeners.addPropertyChangeListener(listener);
      return true;
   }

   public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      if (listeners == null)
      {
         listeners = new PropertyChangeSupport(this);
      }
      listeners.addPropertyChangeListener(propertyName, listener);
      return true;
   }

   public boolean removePropertyChangeListener(PropertyChangeListener listener)
   {
      if (listeners != null)
      {
         listeners.removePropertyChangeListener(listener);
      }
      return true;
   }

   public boolean removePropertyChangeListener(String propertyName,PropertyChangeListener listener)
   {
      if (listeners != null)
      {
         listeners.removePropertyChangeListener(propertyName, listener);
      }
      return true;
   }



   public void removeYou()
   {
      this.setGame(null);
      this.setSelectedBy(null);
      this.setPlayer(null);

      this.withoutNeighbors(this.getNeighbors().clone());


      this.withoutNeighbors(this.getNeighbors().clone());


      this.withoutUnits(this.getUnits().clone());


   }


}