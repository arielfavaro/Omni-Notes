/*******************************************************************************
 * Copyright 2014 Federico Iosue (federico.iosue@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package it.feio.android.omninotes.async;

import android.content.Context;
import android.os.AsyncTask;

import it.feio.android.omninotes.BaseActivity;
import it.feio.android.omninotes.db.DbHelper;
import it.feio.android.omninotes.models.Attachment;
import it.feio.android.omninotes.models.Note;
import it.feio.android.omninotes.utils.StorageManager;

public class DeleteNoteTask extends AsyncTask<Note, Void, Integer> {

	private final Context mContext;


	public DeleteNoteTask(Context mContext) {
		this.mContext = mContext;
	}


	@Override
	protected Integer doInBackground(Note... params) {
		Integer result = null;
		Note note = params[0];

		// Deleting note using DbHelper
		DbHelper db = DbHelper.getInstance(mContext);
		boolean deleted = db.deleteNote(note);

		if (deleted) {
			// Attachment deletion from storage
			boolean attachmentsDeleted = false;
			for (Attachment mAttachment : note.getAttachmentsList()) {
				StorageManager.deleteExternalStoragePrivateFile(mContext, mAttachment.getUri().getLastPathSegment());
			}
			result = deleted && attachmentsDeleted ? note.get_id() : null;
		}
		return result;
	}


	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		BaseActivity.notifyAppWidgets(mContext);
	}
}
