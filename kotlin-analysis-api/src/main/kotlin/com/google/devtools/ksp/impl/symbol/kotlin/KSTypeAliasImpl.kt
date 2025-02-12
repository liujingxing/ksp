/*
 * Copyright 2022 Google LLC
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.devtools.ksp.impl.symbol.kotlin

import com.google.devtools.ksp.KSObjectCache
import com.google.devtools.ksp.symbol.KSExpectActual
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSVisitor
import org.jetbrains.kotlin.analysis.api.symbols.KtTypeAliasSymbol
import org.jetbrains.kotlin.analysis.api.symbols.nameOrAnonymous

class KSTypeAliasImpl private constructor(private val ktTypeAliasSymbol: KtTypeAliasSymbol) :
    KSTypeAlias,
    AbstractKSDeclarationImpl(ktTypeAliasSymbol),
    KSExpectActual by KSExpectActualImpl(ktTypeAliasSymbol) {
    companion object : KSObjectCache<KtTypeAliasSymbol, KSTypeAliasImpl>() {
        fun getCached(ktTypeAliasSymbol: KtTypeAliasSymbol) =
            cache.getOrPut(ktTypeAliasSymbol) { KSTypeAliasImpl(ktTypeAliasSymbol) }
    }

    override val name: KSName by lazy {
        KSNameImpl.getCached(ktTypeAliasSymbol.nameOrAnonymous.asString())
    }

    override val type: KSTypeReference by lazy {
        KSTypeReferenceImpl.getCached(ktTypeAliasSymbol.expandedType)
    }

    override val simpleName: KSName
        get() = name

    override val qualifiedName: KSName? by lazy {
        ktTypeAliasSymbol.classIdIfNonLocal?.asFqNameString()?.let { KSNameImpl.getCached(it) }
    }

    override fun <D, R> accept(visitor: KSVisitor<D, R>, data: D): R {
        return visitor.visitTypeAlias(this, data)
    }
}
