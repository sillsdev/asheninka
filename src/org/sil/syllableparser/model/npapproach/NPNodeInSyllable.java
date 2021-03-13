/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.model.npapproach;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andy Black
 *
 * Object value
 */
public class NPNodeInSyllable implements Cloneable {

	private List<NPNodeInSyllable> nodes = new ArrayList<NPNodeInSyllable>();
	private List<NPSegmentInSyllable> segments = new ArrayList<NPSegmentInSyllable>();
	private NPNodeLevel level = NPNodeLevel.UNKNOWN;
	
	public NPNodeInSyllable() {
	}

	public NPNodeInSyllable(List<NPNodeInSyllable> nodes, List<NPSegmentInSyllable> segments) {
		super();
		this.nodes = nodes;
		this.segments = segments;
	}

	public NPNodeInSyllable(NPNodeLevel level) {
		this.level = level;
	}

	public List<NPNodeInSyllable> getNodes() {
		return nodes;
	}

	public void setNodes(List<NPNodeInSyllable> nodes) {
		this.nodes = nodes;
	}

	public List<NPSegmentInSyllable> getSegments() {
		return segments;
	}

	public void setSegments(List<NPSegmentInSyllable> segments) {
		this.segments = segments;
	}

	public NPNodeLevel getLevel() {
		return level;
	}

	public void setLevel(NPNodeLevel level) {
		this.level = level;
	}

	@Override
	public NPNodeInSyllable clone() {
		List<NPNodeInSyllable> nodesInSyllableNow = new ArrayList<NPNodeInSyllable>(getNodes().size());
		for (NPNodeInSyllable node : getNodes()) {
			nodesInSyllableNow.add(node.clone());
		}
		NPNodeInSyllable nis = new NPNodeInSyllable(nodesInSyllableNow, segments);
		nis.setLevel(getLevel());
		return nis;
	}
}
