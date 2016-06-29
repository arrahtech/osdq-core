/*
 * EqualityOperator.java
 *
 * Created: 2006-04-25
 *
 * Java Data Mining Framework (http://jdmf.sourceforge.net)
 * Copyright (C) 2006  Janusz Marchewa
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 * $Author: quorthon $
 * $LastChangedRevision: 3 $
 * $LastChangedDate: 2006-07-02 21:52:16 +0200 (nie, 02 lip 2006) $
 */
package net.sf.jdmf.data.operators;

/**
 * Represents the equality operator inside rules.
 * 
 * @author quorthon
 */
public enum EqualityOperator {
    EQUAL {
        @Override
        public boolean evaluate( Comparable leftArgument, 
                Comparable rightArgument ) {
            return leftArgument.equals( rightArgument );
        }
        @Override
        public String toString() {
            return "=";
        }
    }, NOT_EQUAL {
        @Override
        public boolean evaluate( Comparable leftArgument, 
                Comparable rightArgument ) {
            return ( leftArgument.equals( rightArgument ) == false );
        }
        @Override
        public String toString() {
            return "!=";
        }
    }, GREATER_THAN {
        @SuppressWarnings("unchecked")
        @Override
        public boolean evaluate( Comparable leftArgument, 
                Comparable rightArgument ) {
            if ( leftArgument.compareTo( rightArgument ) > 0 ) {
                return true;
            }
            return false;
        }
        @Override
        public String toString() {
            return ">";
        }
    }, GREATER_THAN_OR_EQUAL {
        @SuppressWarnings("unchecked")
        @Override
        public boolean evaluate( Comparable leftArgument, 
                Comparable rightArgument ) {
            if ( leftArgument.compareTo( rightArgument ) >= 0 ) {
                return true;
            }
            return false;
        }
        @Override
        public String toString() {
            return ">=";
        }
    }, LOWER_THAN {
        @SuppressWarnings("unchecked")
        @Override
        public boolean evaluate( Comparable leftArgument, 
                Comparable rightArgument ) {
            if ( leftArgument.compareTo( rightArgument ) < 0 ) {
                return true;
            }
            return false;
        }
        @Override
        public String toString() {
            return "<";
        }
    }, LOWER_THAN_OR_EQUAL {
        @SuppressWarnings("unchecked")
        @Override
        public boolean evaluate( Comparable leftArgument, 
                Comparable rightArgument ) {
            if ( leftArgument.compareTo( rightArgument ) <= 0 ) {
                return true;
            }
            return false;
        }
        @Override
        public String toString() {
            return "<=";
        }
    };
    
    public abstract boolean evaluate( 
        Comparable leftArgument, Comparable rightArgument );
}
