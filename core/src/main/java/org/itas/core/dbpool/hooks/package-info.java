/*
 * Copyright 2009 Wallace Wadge
 *
 * This file is part of BoneCP.
 *
 * BoneCP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BoneCP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BoneCP.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.itas.core.dbpool.hooks;

/**
 * Support for event notification on a connection state. 
 * 
 * Use the hook mechanism to register callbacks when a connection's state changes. Most applications will want to extend
 * {@link com.jolbox.bonecp.hooks.AbstractConnectionHook} rather than implementing the {@link com.jolbox.bonecp.hooks.ConnectionHook} interface directly.
 * 
 */