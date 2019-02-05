package de.uniks.liverisk.model;

import java.beans.PropertyChangeSupport;

import java.beans.PropertyChangeListener;

public class Player  
{

   public static final String PROPERTY_name = "name";

   private String name;

   public String getName()
   {
      return name;
   }

   public Player setName(String value)
   {
      if (value == null ? this.name != null : ! value.equals(this.name))
      {
         String oldValue = this.name;
         this.name = value;
         firePropertyChange("name", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_color = "color";

   private String color;

   public String getColor()
   {
      return color;
   }

   public Player setColor(String value)
   {
      if (value == null ? this.color != null : ! value.equals(this.color))
      {
         String oldValue = this.color;
         this.color = value;
         firePropertyChange("color", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_game = "game";

   private Game game = null;

   public Game getGame()
   {
      return this.game;
   }

   public Player setGame(Game value)
   {
      if (this.game != value)
      {
         Game oldValue = this.game;
         if (this.game != null)
         {
            this.game = null;
            oldValue.withoutPlayers(this);
         }
         this.game = value;
         if (value != null)
         {
            value.withPlayers(this);
         }
         firePropertyChange("game", oldValue, value);
      }
      return this;
   }



   public static final String PROPERTY_gameWon = "gameWon";

   private Game gameWon = null;

   public Game getGameWon()
   {
      return this.gameWon;
   }

   public Player setGameWon(Game value)
   {
      if (this.gameWon != value)
      {
         Game oldValue = this.gameWon;
         if (this.gameWon != null)
         {
            this.gameWon = null;
            oldValue.setWinner(null);
         }
         this.gameWon = value;
         if (value != null)
         {
            value.setWinner(this);
         }
         firePropertyChange("gameWon", oldValue, value);
      }
      return this;
   }



   public static final String PROPERTY_currentGame = "currentGame";

   private Game currentGame = null;

   public Game getCurrentGame()
   {
      return this.currentGame;
   }

   public Player setCurrentGame(Game value)
   {
      if (this.currentGame != value)
      {
         Game oldValue = this.currentGame;
         if (this.currentGame != null)
         {
            this.currentGame = null;
            oldValue.setCurrentPlayer(null);
         }
         this.currentGame = value;
         if (value != null)
         {
            value.setCurrentPlayer(this);
         }
         firePropertyChange("currentGame", oldValue, value);
      }
      return this;
   }



   public static final java.util.ArrayList<Platform> EMPTY_platforms = new java.util.ArrayList<Platform>()
   { @Override public boolean add(Platform value){ throw new UnsupportedOperationException("No direct add! Use xy.withPlatforms(obj)"); }};


   public static final String PROPERTY_platforms = "platforms";

   private java.util.ArrayList<Platform> platforms = null;

   public java.util.ArrayList<Platform> getPlatforms()
   {
      if (this.platforms == null)
      {
         return EMPTY_platforms;
      }

      return this.platforms;
   }

   public Player withPlatforms(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withPlatforms(i);
            }
         }
         else if (item instanceof Platform)
         {
            if (this.platforms == null)
            {
               this.platforms = new java.util.ArrayList<Platform>();
            }
            if ( ! this.platforms.contains(item))
            {
               this.platforms.add((Platform)item);
               ((Platform)item).setPlayer(this);
               firePropertyChange("platforms", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }



   public Player withoutPlatforms(Object... value)
   {
      if (this.platforms == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutPlatforms(i);
            }
         }
         else if (item instanceof Platform)
         {
            if (this.platforms.contains(item))
            {
               this.platforms.remove((Platform)item);
               ((Platform)item).setPlayer(null);
               firePropertyChange("platforms", item, null);
            }
         }
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

   public Player withUnits(Object... value)
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
               ((Unit)item).setPlayer(this);
               firePropertyChange("units", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }



   public Player withoutUnits(Object... value)
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
               ((Unit)item).setPlayer(null);
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

   @Override
   public String toString()
   {
      StringBuilder result = new StringBuilder();

      result.append(" ").append(this.getName());
      result.append(" ").append(this.getColor());


      return result.substring(1);
   }

   public void removeYou()
   {
      this.setGame(null);
      this.setGameWon(null);
      this.setCurrentGame(null);

      this.withoutPlatforms(this.getPlatforms().clone());


      this.withoutUnits(this.getUnits().clone());


   }


}