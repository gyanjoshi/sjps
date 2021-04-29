/**
 * 
 */
package com.example.projectx.model;

/**
 * @author Gyan Prakash Joshi
 * Mar 8, 2021
 * This class encapsulates article reviewer object. Article review may or may not be the application user (edtor).
 * If reviewer is executive editor, it will be AppUser with Editor role. If the reviewer is non-executive editor, It will not be 
 * AppUser, it will simply be Editor object.
 *
 */

public interface Reviewer {
	
	public  boolean isAppUser();
	public  Reviewer getReviewer();	

}
