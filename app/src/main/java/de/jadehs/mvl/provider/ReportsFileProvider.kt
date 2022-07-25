package de.jadehs.mvl.provider

import androidx.core.content.FileProvider
import de.jadehs.mvl.R

class ReportsFileProvider : FileProvider(R.xml.file_paths) {

    companion object {
        const val AUTHORITY = "de.jadehs.provider.ReportsFileProvider"
    }
}