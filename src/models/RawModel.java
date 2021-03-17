package models;

public class RawModel {
	
	private final int vaoID;
	private final int[] vboIds;
	private final int vertexCount;
	
	public RawModel(final int vaoID, final int[] vboIds, final int vertexCount){
		this.vaoID = vaoID;
		this.vboIds = vboIds;
		this.vertexCount = vertexCount;
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public int[] getVboIds() {
		return vboIds;
	}
}
