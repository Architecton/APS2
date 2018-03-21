public class Matrix {
	int[][] matrix;
	
	public Matrix(String params) {
		// split entered data by ; which separates rows
		String[] rows = params.split(";");
		
		boolean firstRow = true;
		int RowLength = 0;
		
		for(int i = 0; i < rows.length; i++) {
			// replace all whitespace chars with splitting symbol .
			rows[i] = rows[i].trim();
			rows[i] = rows[i].replaceAll("\\s+", "~");
			// split row by .
			String[] processedRow = rows[i].split("~");
	
			// if processing first row set as matrix row length and initialize array
			if(firstRow) {
				RowLength = processedRow.length;
				firstRow = false;
				matrix = new int[RowLength][rows.length];
			} else {
				// if row not conforming to matrix dimensions, throw error
				if(processedRow.length != RowLength) {
					throw new Error("matrix dimension mismatch");
				}
			}
			
			// go over parsed row and enter it into matrix
			for(int j = 0; j < processedRow.length; j++) {
				matrix[i][j] = Integer.parseInt(processedRow[j]);
			}	
		}
	}

	// get: returns elements of the array denoted by params in MATLAB syntax
	public int[][] get(String params) {
		return null;
	}
	
	// mult: multiply this matrix with passed matrix B and return resulting matrix
	public Matrix mult(Matrix B) {
		return null;
	}
	
	public void print() {
		for(int i = 0; i < this.matrix.length; i++) {
			for(int j = 0; j < this.matrix[i].length; j++) {
				System.out.printf((j == this.matrix[i].length - 1) ? "%d%n" : "%d ", this.matrix[i][j]);
			}
		}
	}
}